package com.xxsx.fantasyearth;

import com.xxsx.fantasyearth.entity.ContractGolemEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/** F-037 Construct Core: summons FB-09 contract_golem on ground if player knows K-MAG-21, charge >= 240, 2x3x2 clear space, limit 1 per player. */
public class ConstructCoreItem extends MagicMaterialItem {
    public ConstructCoreItem(Properties properties, String hintKey) {
        super(properties, hintKey);
    }

    public static String checkSummonFailure(ServerLevel level, ServerPlayer player, BlockPos spawnPos, ItemStack stack) {
        if (!FantasyLoreIntegration.knowsConstructs(player)) {
            return "lore_locked";
        }
        ConstructCoreComponent comp = ConstructCoreComponent.read(stack);
        if (comp.chargeAeu() + 0.001D < ConstructCoreComponent.MAX_CHARGE_AEU) {
            return "no_charge";
        }
        if (!hasClearSpace2x3x2(level, spawnPos)) {
            return "no_space";
        }
        if (hasActiveGolem(level, player.getUUID())) {
            return "limit_reached";
        }
        return "";
    }

    public static boolean hasClearSpace2x3x2(Level level, BlockPos base) {
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 3; dy++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos check = base.offset(dx, dy, dz);
                    if (!level.getBlockState(check).getCollisionShape(level, check).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean hasActiveGolem(ServerLevel level, UUID ownerId) {
        AABB searchBox = new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000);
        List<ContractGolemEntity> existing = level.getEntities(
                EntityTypeTest.forClass(ContractGolemEntity.class),
                searchBox,
                g -> g.isAlive() && ownerId.equals(g.getOwnerUuid()));
        return !existing.isEmpty();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel) || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        ItemStack stack = context.getItemInHand();
        BlockPos spawnPos = context.getClickedPos().above();
        String fail = checkSummonFailure(serverLevel, serverPlayer, spawnPos, stack);
        if (!fail.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.construct_core." + fail)
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        ContractGolemEntity golem = FantasyEarth.CONTRACT_GOLEM.get().create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
        if (golem == null) {
            return InteractionResult.FAIL;
        }
        golem.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        golem.setOwnerUuid(serverPlayer.getUUID());
        golem.setLifetime(ContractGolemEntity.DEFAULT_LIFETIME_TICKS);
        if (!serverLevel.addFreshEntity(golem)) {
            return InteractionResult.FAIL;
        }
        ConstructCoreComponent comp = ConstructCoreComponent.read(stack).withBoundGolem(golem.getUUID());
        stack.set(FantasyEarth.CONSTRUCT_CORE_COMPONENT.get(), comp);
        if (!serverPlayer.getAbilities().instabuild) {
            stack.shrink(1);
        }
        serverLevel.playSound(null, spawnPos, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.9F, 0.9F);
        serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.construct_core.summoned")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, lines, flag);
        ConstructCoreComponent comp = ConstructCoreComponent.read(stack);
        lines.accept(Component.translatable("tooltip.fantasy_earth.construct_core.charge",
                ArcanaPower.format(comp.chargeAeu()), ArcanaPower.format(ConstructCoreComponent.MAX_CHARGE_AEU))
                .withStyle(ChatFormatting.AQUA));
    }
}
