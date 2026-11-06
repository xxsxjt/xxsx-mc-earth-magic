package com.xxsx.fantasyearth;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** F-035 Ritual Circle Marker: flat chalk mark on the ground; right-click to remove. */
public class RitualCircleMarkerBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);

    public RitualCircleMarkerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return removeMarker(level, pos, player);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(FantasyEarth.RITUAL_CIRCLE_MARKER.asItem())) {
            return removeMarker(level, pos, player);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult removeMarker(Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        if (!player.getAbilities().instabuild) {
            player.getInventory().placeItemBackInInventory(new ItemStack(FantasyEarth.RITUAL_CIRCLE_MARKER.get()));
        }
        level.playSound(null, pos, SoundEvents.CALCITE_PLACE, SoundSource.BLOCKS, 0.6F, 1.2F);
        return InteractionResult.SUCCESS_SERVER;
    }
}
