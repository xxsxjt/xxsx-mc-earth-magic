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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** F-034 Offering Bowl: holds an offering for ritual circle stability & sacrifice. */
public class OfferingBowlBlock extends Block {
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);

    public OfferingBowlBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FILLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            player.swing(InteractionHand.MAIN_HAND);
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(FILLED)) {
            level.setBlock(pos, state.setValue(FILLED, false), Block.UPDATE_CLIENTS);
            player.getInventory().placeItemBackInInventory(new ItemStack(FantasyEarth.ARCANE_DUST.get()));
            player.swing(InteractionHand.MAIN_HAND, true);
            if (level instanceof ServerLevel serverLevel) {
                ArcaneVfx.moteBurst(serverLevel, pos.getX() + 0.5D, pos.getY() + 0.45D, pos.getZ() + 0.5D, 6, 0.2D);
            }
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.7F, 1.0F);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        if (!state.getValue(FILLED) && !stack.isEmpty()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.setBlock(pos, state.setValue(FILLED, true), Block.UPDATE_CLIENTS);
            player.swing(hand, true);
            if (level instanceof ServerLevel serverLevel) {
                ArcaneVfx.sparkBurst(serverLevel, pos.getX() + 0.5D, pos.getY() + 0.45D, pos.getZ() + 0.5D, 8, 0.22D);
            }
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.7F, 1.0F);
            return InteractionResult.SUCCESS_SERVER;
        }
        return useWithoutItem(state, level, pos, player, hitResult);
    }
}
