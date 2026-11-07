package com.xxsx.fantasyearth;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** F-033 Ritual Candle: lit boolean state, +0.05 ritual stability per lit candle (max 4 counted). */
public class RitualCandleBlock extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final VoxelShape SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);

    public RitualCandleBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return toggleLit(state, level, pos, player, InteractionHand.MAIN_HAND);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hitResult) {
        return toggleLit(state, level, pos, player, hand);
    }

    private InteractionResult toggleLit(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        boolean next = !state.getValue(LIT);
        level.setBlock(pos, state.setValue(LIT, next), Block.UPDATE_CLIENTS);
        player.swing(hand, true);
        if (next && level instanceof ServerLevel serverLevel) {
            ArcaneVfx.sparkBurst(serverLevel, pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D, 6, 0.15D);
        }
        level.playSound(null, pos, next ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.CANDLE_EXTINGUISH,
                SoundSource.BLOCKS, 0.7F, 1.1F);
        return InteractionResult.SUCCESS_SERVER;
    }
}
