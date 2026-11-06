package com.xxsx.fantasyearth;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class MagicStructures {
    private MagicStructures() {
    }

    public static int tierFor(MagicMachineBlock.Kind kind, Level level, BlockPos pos) {
        if (kind == MagicMachineBlock.Kind.RITUAL_PEDESTAL && isFormalRitualCircle(level, pos)) {
            int candles = countLitCandles(level, pos);
            return candles >= 2 ? 2 : 1;
        }
        return 0;
    }

    public static int countLitCandles(Level level, BlockPos center) {
        int count = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockState state = level.getBlockState(center.offset(dx, 0, dz));
                if (state.getBlock() instanceof RitualCandleBlock && state.getValue(RitualCandleBlock.LIT)) {
                    count++;
                    if (count >= 4) {
                        return 4;
                    }
                }
            }
        }
        return count;
    }

    public static boolean isFormalRitualCircle(Level level, BlockPos center) {
        if (!is(level, center, FantasyEarth.RITUAL_PEDESTAL.get())) {
            return false;
        }
        boolean legacy3x3 = isAetherFocus(level, center.north().east())
                && isAetherFocus(level, center.north().west())
                && isAetherFocus(level, center.south().east())
                && isAetherFocus(level, center.south().west())
                && isRitualMark(level, center.north())
                && isRitualMark(level, center.south())
                && isRitualMark(level, center.east())
                && isRitualMark(level, center.west());
        if (legacy3x3) {
            return true;
        }
        boolean cardinalPedestals = is(level, center.north(2), FantasyEarth.RITUAL_PEDESTAL.get())
                && is(level, center.south(2), FantasyEarth.RITUAL_PEDESTAL.get())
                && is(level, center.east(2), FantasyEarth.RITUAL_PEDESTAL.get())
                && is(level, center.west(2), FantasyEarth.RITUAL_PEDESTAL.get());
        boolean diagonalPedestals = is(level, center.north(2).east(2), FantasyEarth.RITUAL_PEDESTAL.get())
                && is(level, center.north(2).west(2), FantasyEarth.RITUAL_PEDESTAL.get())
                && is(level, center.south(2).east(2), FantasyEarth.RITUAL_PEDESTAL.get())
                && is(level, center.south(2).west(2), FantasyEarth.RITUAL_PEDESTAL.get());
        if (!cardinalPedestals && !diagonalPedestals) {
            return false;
        }
        int markers = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.max(Math.abs(dx), Math.abs(dz)) == 2) {
                    Block b = level.getBlockState(center.offset(dx, 0, dz)).getBlock();
                    if (b == FantasyEarth.RITUAL_CIRCLE_MARKER.get() || isRitualMark(level, center.offset(dx, 0, dz))) {
                        markers++;
                    }
                }
            }
        }
        return markers >= 8;
    }

    public static Optional<BlockPos> findFormalRitualCenter(Level level, BlockPos clicked) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos center = clicked.offset(dx, 0, dz);
                if (isFormalRitualCircle(level, center)) {
                    return Optional.of(center);
                }
            }
        }
        return Optional.empty();
    }

    private static boolean isAetherFocus(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block == FantasyEarth.AETHER_CRYSTAL_CLUSTER.get()
                || block == Blocks.AMETHYST_CLUSTER;
    }

    private static boolean isRitualMark(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block == FantasyEarth.RUNE_CARVING_TABLE.get()
                || block == FantasyEarth.ALCHEMY_TABLE.get()
                || block == FantasyEarth.RITUAL_CIRCLE_MARKER.get()
                || block == FantasyEarth.RITUAL_CANDLE.get()
                || block == FantasyEarth.OFFERING_BOWL.get()
                || block == Blocks.BOOKSHELF
                || block == Blocks.GLOWSTONE
                || block == Blocks.REDSTONE_BLOCK;
    }

    private static boolean is(Level level, BlockPos pos, Block block) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() == block;
    }
}
