package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** F-001 Aether Crystal: right-click extracts 20 AEU from local field; sneak+right-click converts to 6 MP. */
public class AetherCrystalItem extends MagicMaterialItem {
    public AetherCrystalItem(Properties properties, String hintKey) {
        super(properties, hintKey);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        AetherCrystalComponent comp = AetherCrystalComponent.read(stack);
        if (player.isShiftKeyDown()) {
            double requiredAeu = AetherLedger.aeuCostOfMana(AetherCrystalComponent.MANA_BATCH_MP);
            if (comp.chargeAeu() + 0.001D < requiredAeu) {
                player.sendSystemMessage(Component.translatable("message.fantasy_earth.aether_crystal.empty")
                        .withStyle(ChatFormatting.RED));
                return InteractionResult.SUCCESS_SERVER;
            }
            double before = ArcanaPower.getCurrentMana(player);
            ArcanaPower.setCurrentMana(player, before + AetherCrystalComponent.MANA_BATCH_MP);
            double restored = ArcanaPower.getCurrentMana(player) - before;
            if (restored <= 0.0D) {
                player.sendSystemMessage(Component.translatable("message.fantasy_earth.mana_consumable.full",
                        ArcanaPower.format(before), ArcanaPower.format(ArcanaPower.getMaxMana(player)))
                        .withStyle(ChatFormatting.YELLOW));
                return InteractionResult.SUCCESS_SERVER;
            }
            stack.set(FantasyEarth.AETHER_CRYSTAL_COMPONENT.get(),
                    comp.withCharge(comp.chargeAeu() - requiredAeu, comp.signature()));
            player.swing(hand, true);
            if (level instanceof ServerLevel serverLevel) {
                ArcaneVfx.moteBurst(serverLevel, player.getX(), player.getY() + 1.0D, player.getZ(), 16, 0.45D);
                ArcaneVfx.shardBurst(serverLevel, player.getX(), player.getY() + 0.9D, player.getZ(), 10, 0.35D);
                serverLevel.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                        SoundSource.PLAYERS, 0.85F, 1.25F);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                ArcaneNetwork.broadcastVisual(serverPlayer, ArcaneVisualAction.MANA_RECOVERY);
                MagicJourney.complete(serverPlayer, MagicJourney.Milestone.MANA_RECOVERY);
            }
            player.sendSystemMessage(Component.translatable("message.fantasy_earth.aether_crystal.converted",
                    ArcanaPower.format(restored),
                    ArcanaPower.format(comp.chargeAeu() - requiredAeu),
                    ArcanaPower.format(comp.capacityAeu())).withStyle(ChatFormatting.LIGHT_PURPLE));
            return InteractionResult.SUCCESS_SERVER;
        }

        if (comp.chargeAeu() >= AetherCrystalComponent.CAPACITY_AEU - 0.001D) {
            AetherChunkField.disturb(level, player.blockPosition(), AetherCrystalComponent.CHARGE_BATCH_AEU * 0.5D);
            player.sendSystemMessage(Component.translatable("message.fantasy_earth.aether_crystal.full")
                    .withStyle(ChatFormatting.YELLOW));
            return InteractionResult.SUCCESS_SERVER;
        }
        AetherChunkField.Reading reading = AetherChunkField.read(level, player.blockPosition());
        double available = AetherLedger.availableAeu(reading.value(), reading.disturbance());
        if (available < 1.0D) {
            player.sendSystemMessage(Component.translatable("message.fantasy_earth.aether_crystal.no_field")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS_SERVER;
        }
        double extracted = Math.min(AetherCrystalComponent.CHARGE_BATCH_AEU, available);
        double space = AetherCrystalComponent.CAPACITY_AEU - comp.chargeAeu();
        double accepted = Math.min(space, extracted * AetherLedger.CAPTURE_EFFICIENCY);
        double rejected = Math.max(0.0D, extracted - accepted);
        AetherChunkField.disturbAeu(level, player.blockPosition(), extracted);
        if (rejected > 0.0D) {
            AetherChunkField.disturb(level, player.blockPosition(), rejected * 0.5D);
        }
        String sig = AetherCrystalComponent.computeSignature(level, player.blockPosition(), reading.mainSourceKey());
        AetherCrystalComponent updated = comp.withCharge(comp.chargeAeu() + accepted, sig);
        stack.set(FantasyEarth.AETHER_CRYSTAL_COMPONENT.get(), updated);
        player.swing(hand, true);
        if (level instanceof ServerLevel serverLevel) {
            ArcaneVfx.moteBurst(serverLevel, player.getX(), player.getY() + 1.0D, player.getZ(), 14, 0.4D);
            ArcaneVfx.shardBurst(serverLevel, player.getX(), player.getY() + 1.0D, player.getZ(), 8, 0.35D);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            ArcaneNetwork.broadcastVisual(serverPlayer, ArcaneVisualAction.AETHER_PULSE);
        }
        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7F, 1.2F);
        player.sendSystemMessage(Component.translatable("message.fantasy_earth.aether_crystal.charged",
                ArcanaPower.format(updated.chargeAeu()), ArcanaPower.format(updated.capacityAeu()))
                .withStyle(ChatFormatting.AQUA));
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, lines, flag);
        AetherCrystalComponent comp = AetherCrystalComponent.read(stack);
        lines.accept(Component.translatable("tooltip.fantasy_earth.aether_crystal.charge",
                ArcanaPower.format(comp.chargeAeu()), ArcanaPower.format(comp.capacityAeu()))
                .withStyle(ChatFormatting.AQUA));
        if (!comp.signature().isBlank()) {
            lines.accept(Component.translatable("tooltip.fantasy_earth.aether_crystal.signature", comp.signature())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
