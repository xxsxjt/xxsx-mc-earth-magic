package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/** F-010 Aether Battery block: capacity 240 AEU, stores and discharges AEU to crystals/foci. */
public class AetherBatteryBlock extends Block {
    public static final double CAPACITY_AEU = 240.0D;
    public static final double INPUT_RATE = 40.0D;
    public static final double OUTPUT_RATE = 30.0D;
    /** 0..6 representing 0..240 AEU in 40 AEU steps. */
    public static final IntegerProperty CHARGE_STEP = IntegerProperty.create("charge_step", 0, 6);

    public AetherBatteryBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CHARGE_STEP, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE_STEP);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return handleInteract(state, level, pos, player, InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hitResult) {
        return handleInteract(state, level, pos, player, hand, stack);
    }

    private InteractionResult handleInteract(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (level.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        int step = state.getValue(CHARGE_STEP);
        double stored = step * 40.0D;
        if (stack.is(FantasyEarth.AETHER_CRYSTAL.get())) {
            AetherCrystalComponent comp = AetherCrystalComponent.read(stack);
            if (comp.chargeAeu() >= 40.0D && step < 6) {
                stack.set(FantasyEarth.AETHER_CRYSTAL_COMPONENT.get(), comp.withCharge(comp.chargeAeu() - 40.0D, comp.signature()));
                level.setBlock(pos, state.setValue(CHARGE_STEP, step + 1), Block.UPDATE_CLIENTS);
                stored = (step + 1) * 40.0D;
            } else if (comp.chargeAeu() <= 20.0D && step > 0) {
                stack.set(FantasyEarth.AETHER_CRYSTAL_COMPONENT.get(), comp.withCharge(comp.chargeAeu() + 40.0D, comp.signature()));
                level.setBlock(pos, state.setValue(CHARGE_STEP, step - 1), Block.UPDATE_CLIENTS);
                stored = (step - 1) * 40.0D;
            }
        } else if (player.isShiftKeyDown() && step < 6 && level instanceof ServerLevel serverLevel) {
            AetherChunkField.Reading reading = AetherChunkField.read(serverLevel, pos);
            double avail = AetherLedger.availableAeu(reading.value(), reading.disturbance());
            if (avail >= 40.0D) {
                AetherChunkField.disturbAeu(serverLevel, pos, 40.0D);
                level.setBlock(pos, state.setValue(CHARGE_STEP, step + 1), Block.UPDATE_CLIENTS);
                stored = (step + 1) * 40.0D;
            }
        }
        player.swing(hand, true);
        if (level instanceof ServerLevel serverLevel) {
            ArcaneVfx.moteBurst(serverLevel, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D, 8, 0.3D);
            serverLevel.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.7F, 1.1F);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.aether_battery.status",
                    ArcanaPower.format(stored), ArcanaPower.format(CAPACITY_AEU)).withStyle(ChatFormatting.AQUA));
        }
        return InteractionResult.SUCCESS_SERVER;
    }
}
