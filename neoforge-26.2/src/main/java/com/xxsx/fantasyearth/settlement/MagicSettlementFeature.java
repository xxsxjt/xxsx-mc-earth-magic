package com.xxsx.fantasyearth.settlement;

import com.mojang.serialization.Codec;
import com.xxsx.fantasyearth.FantasyEarth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

/**
 * Magic settlement feature.
 *
 * <p>Every settlement is rotated in quarter turns and picks one of three
 * layouts plus a palette, so two hamlets in the same biome do not look the
 * same. Structures are deliberately small and sparse: they are landmarks,
 * not villages.</p>
 */
public final class MagicSettlementFeature extends Feature<NoneFeatureConfiguration> {
    private static final int UPDATE_FLAGS = 2;
    private final Type type;

    public MagicSettlementFeature(Codec<NoneFeatureConfiguration> codec, Type type) {
        super(codec);
        this.type = type;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos center = context.origin();
        while (level.isEmptyBlock(center) && center.getY() > level.getMinY() + 2) {
            center = center.below();
        }
        center = center.above();
        if (level.isEmptyBlock(center.below())) {
            return false;
        }
        Pose pose = new Pose(center, random.nextInt(4));
        int variant = random.nextInt(4);
        Palette palette = Palette.pick(type, random);
        prepareSite(level, center, type.radius());
        switch (type) {
            case WITCH_HAMLET -> buildWitchHamlet(level, pose, variant, palette, random);
            case GOBLIN_EXCHANGE -> buildGoblinExchange(level, pose, variant, palette, random);
            case ACADEMY_OUTPOST -> buildAcademyOutpost(level, pose, variant, palette, random);
            case DWARVEN_DELVING -> buildDwarvenDelving(level, pose, variant, palette, random);
            case ELVEN_GROVE -> buildElvenGrove(level, pose, variant, palette, random);
        }
        scatterGrowth(level, pose, type.radius(), random);
        level.setBlock(center, FantasyEarth.SETTLEMENT_ANCHOR.get().defaultBlockState(), UPDATE_FLAGS);
        if (level.getBlockEntity(center) instanceof MagicSettlementAnchorBlockEntity anchor) {
            anchor.configure(type.id());
        }
        return true;
    }

    private static void prepareSite(WorldGenLevel level, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                level.setBlock(center.offset(x, -1, z), Blocks.STONE_BRICKS.defaultBlockState(), UPDATE_FLAGS);
                for (int y = 0; y <= 7; y++) {
                    level.setBlock(center.offset(x, y, z), Blocks.AIR.defaultBlockState(), UPDATE_FLAGS);
                }
            }
        }
    }

    private void buildWitchHamlet(WorldGenLevel level, Pose pose, int variant, Palette palette, RandomSource random) {
        fillFloor(level, pose, 4, palette.floor());
        switch (variant) {
            case 0 -> {
                hut(level, pose, -3, 2, palette, 3, random);
                hut(level, pose, 3, 2, palette, 3, random);
                level.setBlock(pose.at(-2, -2), Blocks.CAULDRON.defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(2, -2), FantasyEarth.ARCANE_FOCUS_MAT.get().defaultBlockState(), UPDATE_FLAGS);
                garden(level, pose, 0, -3, random);
            }
            case 1 -> {
                hut(level, pose, 0, 1, palette, 4, random);
                hut(level, pose, -3, -2, palette, 2, random);
                level.setBlock(pose.at(3, -2), Blocks.BREWING_STAND.defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(3, 1), FantasyEarth.ALCHEMY_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
            }
            case 2 -> {
                stoneCircle(level, pose, 2, palette, random);
                level.setBlock(pose.at(-3, 1), FantasyEarth.RUNE_CARVING_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(3, 1), Blocks.CAULDRON.defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(0, -2), FantasyEarth.AETHER_CRYSTAL_CLUSTER.get().defaultBlockState(), UPDATE_FLAGS);
            }
            default -> ruin(level, pose, palette, random, 3);
        }
        perimeterFence(level, pose, 4, palette, random, 3);
        paths(level, pose, palette, random);
        if (variant != 1) {
            level.setBlock(pose.at(-2, 1), FantasyEarth.ARCANE_FOCUS_MAT.get().defaultBlockState(), UPDATE_FLAGS);
        }
        stock(level, pose.at(-4, 2), random,
                new ItemStack(FantasyEarth.ARCANE_DUST.get(), 6 + random.nextInt(8)),
                new ItemStack(FantasyEarth.RUNE_INK.get(), 4 + random.nextInt(6)),
                new ItemStack(FantasyEarth.CRYSTALLIZED_MANA_SALT.get(), 1 + random.nextInt(3)));
        stock(level, pose.at(4, 2), random,
                new ItemStack(FantasyEarth.RITUAL_CHALK.get(), 4 + random.nextInt(6)),
                new ItemStack(FantasyEarth.AETHER_GLASS.get(), 1 + random.nextInt(4)),
                new ItemStack(Items.AMETHYST_SHARD, 2 + random.nextInt(5)));
        placeBoard(level, pose.at(0, -4));
        cornerLights(level, pose, 4, palette.light());
    }

    private void buildGoblinExchange(WorldGenLevel level, Pose pose, int variant, Palette palette, RandomSource random) {
        fillFloor(level, pose, 4, palette.floor());
        switch (variant) {
            case 0 -> {
                stall(level, pose, -2, 1, palette);
                stall(level, pose, 2, 1, palette);
                line(level, pose, -4, 4, 9, true, Blocks.RAIL.defaultBlockState());
            }
            case 1 -> {
                for (int index = 0; index < 4; index++) {
                    int x = -3 + random.nextInt(7);
                    int z = -2 + random.nextInt(5);
                    scrapPile(level, pose, x, z, palette, random);
                }
                level.setBlock(pose.at(0, 3), FantasyEarth.RUNE_CARVING_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
            }
            case 2 -> {
                gate(level, pose, 0, 3, palette, 4);
                line(level, pose, -1, 3, 5, false, Blocks.RAIL.defaultBlockState());
                level.setBlock(pose.at(-3, -1), FantasyEarth.ALCHEMY_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
            }
            default -> ruin(level, pose, palette, random, 3);
        }
        perimeterFence(level, pose, 4, palette, random, 5);
        paths(level, pose, palette, random);
        level.setBlock(pose.at(0, -2), FantasyEarth.AETHER_CRYSTAL_CLUSTER.get().defaultBlockState(), UPDATE_FLAGS);
        stock(level, pose.at(-3, 2), random,
                new ItemStack(FantasyEarth.RUNE_COPPER_PLATE.get(), 2 + random.nextInt(4)),
                new ItemStack(FantasyEarth.ARCANE_DUST.get(), 4 + random.nextInt(6)));
        stock(level, pose.at(0, 2), random,
                new ItemStack(FantasyEarth.AETHER_CRYSTAL.get(), 1 + random.nextInt(3)),
                new ItemStack(FantasyEarth.FAMILIAR_CONTRACT.get(), 1 + random.nextInt(2)));
        stock(level, pose.at(3, 2), random,
                new ItemStack(FantasyEarth.RUNE_INK.get(), 3 + random.nextInt(5)),
                new ItemStack(FantasyEarth.CRYSTALLIZED_MANA_SALT.get(), 1 + random.nextInt(3)));
        placeBoard(level, pose.at(0, -4));
        cornerLights(level, pose, 4, palette.light());
    }

    private void buildAcademyOutpost(WorldGenLevel level, Pose pose, int variant, Palette palette, RandomSource random) {
        fillFloor(level, pose, 5, palette.floor());
        switch (variant) {
            case 0 -> {
                for (int[] corner : new int[][]{{-4, -4}, {4, -4}, {-4, 4}, {4, 4}}) {
                    column(level, pose.at(corner[0], corner[1]), 5, palette.wall());
                    level.setBlock(pose.at(corner[0], corner[1]).above(5),
                            Blocks.AMETHYST_CLUSTER.defaultBlockState(), UPDATE_FLAGS);
                }
                level.setBlock(pose.at(0, -3), Blocks.ENCHANTING_TABLE.defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(0, 3), Blocks.LECTERN.defaultBlockState(), UPDATE_FLAGS);
                libraryWall(level, pose, -3, 3, palette);
            }
            case 1 -> {
                tower(level, pose, -2, -2, 6, palette);
                level.setBlock(pose.at(3, 2), FantasyEarth.RITUAL_PEDESTAL.get().defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(-3, 3), Blocks.LECTERN.defaultBlockState(), UPDATE_FLAGS);
            }
            case 2 -> {
                stoneCircle(level, pose, 3, palette, random);
                level.setBlock(pose.at(0, 1), Blocks.ENCHANTING_TABLE.defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(-3, -1), FantasyEarth.RUNE_CARVING_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
            }
            default -> {
                ruin(level, pose, palette, random, 4);
                level.setBlock(pose.at(0, 0), Blocks.ENCHANTING_TABLE.defaultBlockState(), UPDATE_FLAGS);
            }
        }
        perimeterFence(level, pose, 5, palette, random, 6);
        paths(level, pose, palette, random);
        level.setBlock(pose.at(2, 2), FantasyEarth.ALCHEMY_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
        level.setBlock(pose.at(0, -1), FantasyEarth.AETHER_CRYSTAL_CLUSTER.get().defaultBlockState(), UPDATE_FLAGS);
        stock(level, pose.at(-4, 0), random,
                new ItemStack(FantasyEarth.ARCANE_DUST.get(), 8 + random.nextInt(8)),
                new ItemStack(FantasyEarth.ARCANE_INITIATION_NOTES.get(), 1));
        stock(level, pose.at(4, 0), random,
                new ItemStack(FantasyEarth.RITUAL_CHALK.get(), 6 + random.nextInt(6)),
                new ItemStack(FantasyEarth.AETHER_CRYSTAL.get(), 1 + random.nextInt(3)));
        placeBoard(level, pose.at(4, -4));
        cornerLights(level, pose, 5, palette.light());
    }

    private void buildDwarvenDelving(WorldGenLevel level, Pose pose, int variant, Palette palette, RandomSource random) {
        fillFloor(level, pose, 5, palette.floor());
        switch (variant) {
            case 0 -> {
                gate(level, pose, 0, 4, palette, 5);
                level.setBlock(pose.at(-2, 1), FantasyEarth.RUNE_CARVING_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(2, 1), FantasyEarth.RITUAL_PEDESTAL.get().defaultBlockState(), UPDATE_FLAGS);
            }
            case 1 -> {
                for (int index = 0; index < 4; index++) {
                    int x = -3 + random.nextInt(7);
                    int z = -3 + random.nextInt(7);
                    orePile(level, pose, x, z, random);
                }
                line(level, pose, -3, 2, 7, true, Blocks.RAIL.defaultBlockState());
            }
            case 2 -> {
                storehouse(level, pose, palette, random);
                level.setBlock(pose.at(3, 0), FantasyEarth.RITUAL_PEDESTAL.get().defaultBlockState(), UPDATE_FLAGS);
            }
            default -> {
                ruin(level, pose, palette, random, 4);
                orePile(level, pose, -3, 2, random);
            }
        }
        perimeterFence(level, pose, 5, palette, random, 5);
        paths(level, pose, palette, random);
        level.setBlock(pose.at(-4, -3), FantasyEarth.AETHER_CRYSTAL_CLUSTER.get().defaultBlockState(), UPDATE_FLAGS);
        stock(level, pose.at(-4, 2), random,
                new ItemStack(FantasyEarth.RUNE_COPPER_PLATE.get(), 2 + random.nextInt(4)),
                new ItemStack(FantasyEarth.ARCANE_DUST.get(), 6 + random.nextInt(6)));
        stock(level, pose.at(4, 2), random,
                new ItemStack(FantasyEarth.CRYSTALLIZED_MANA_SALT.get(), 2 + random.nextInt(4)),
                new ItemStack(FantasyEarth.AETHER_GLASS.get(), 2 + random.nextInt(3)));
        placeBoard(level, pose.at(0, -4));
        cornerLights(level, pose, 5, palette.light());
    }

    private void buildElvenGrove(WorldGenLevel level, Pose pose, int variant, Palette palette, RandomSource random) {
        fillFloor(level, pose, 5, palette.floor());
        switch (variant) {
            case 0 -> {
                livingArch(level, pose, 0, 3);
                garden(level, pose, 0, -2, random);
                level.setBlock(pose.at(0, 1), FantasyEarth.ARCANE_FOCUS_MAT.get().defaultBlockState(), UPDATE_FLAGS);
            }
            case 1 -> {
                songHall(level, pose, palette);
                level.setBlock(pose.at(0, 0), Blocks.LECTERN.defaultBlockState(), UPDATE_FLAGS);
                level.setBlock(pose.at(2, 2), FantasyEarth.ALCHEMY_TABLE.get().defaultBlockState(), UPDATE_FLAGS);
            }
            case 2 -> {
                moonObservatory(level, pose, random);
                level.setBlock(pose.at(0, 0), FantasyEarth.RITUAL_PEDESTAL.get().defaultBlockState(), UPDATE_FLAGS);
            }
            default -> {
                ruin(level, pose, palette, random, 4);
                garden(level, pose, 0, 0, random);
            }
        }
        perimeterFence(level, pose, 5, palette, random, 7);
        paths(level, pose, palette, random);
        level.setBlock(pose.at(-3, 3), FantasyEarth.AETHER_CRYSTAL_CLUSTER.get().defaultBlockState(), UPDATE_FLAGS);
        stock(level, pose.at(-4, 1), random,
                new ItemStack(FantasyEarth.ARCANE_DUST.get(), 6 + random.nextInt(6)),
                new ItemStack(FantasyEarth.AETHER_GLASS.get(), 2 + random.nextInt(4)));
        stock(level, pose.at(4, 1), random,
                new ItemStack(FantasyEarth.CRYSTALLIZED_MANA_SALT.get(), 2 + random.nextInt(3)),
                new ItemStack(Items.AMETHYST_SHARD, 3 + random.nextInt(6)));
        placeBoard(level, pose.at(0, -4));
        cornerLights(level, pose, 5, palette.light());
    }

    private static void hut(WorldGenLevel level, Pose pose, int x, int z, Palette palette, int size, RandomSource random) {
        for (int dx = -size; dx <= size; dx++) {
            for (int dz = -size; dz <= size; dz++) {
                level.setBlock(pose.at(x + dx, z + dz), palette.floor(), UPDATE_FLAGS);
            }
        }
        for (int[] corner : new int[][]{{-size, -size}, {size, -size}, {-size, size}, {size, size}}) {
            column(level, pose.at(x + corner[0], z + corner[1]), 1, palette.wall());
        }
        for (int dx = -size - 1; dx <= size + 1; dx++) {
            for (int dz = -size - 1; dz <= size + 1; dz++) {
                level.setBlock(pose.at(x + dx, z + dz).above(3), palette.roof(), UPDATE_FLAGS);
            }
        }
        if (random.nextBoolean()) {
            level.setBlock(pose.at(x, z).above(3), palette.accent(), UPDATE_FLAGS);
        }
    }

    private static void stall(WorldGenLevel level, Pose pose, int x, int z, Palette palette) {
        for (int[] corner : new int[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}}) {
            column(level, pose.at(x + corner[0], z + corner[1]), 3, palette.post());
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(pose.at(x + dx, z + dz).above(3), palette.roof(), UPDATE_FLAGS);
            }
        }
        level.setBlock(pose.at(x, z), Blocks.BARREL.defaultBlockState(), UPDATE_FLAGS);
    }

    private static void scrapPile(WorldGenLevel level, Pose pose, int x, int z, Palette palette, RandomSource random) {
        int height = 1 + random.nextInt(3);
        for (int y = 0; y < height; y++) {
            level.setBlock(pose.at(x, z).above(y),
                    random.nextBoolean() ? palette.accent() : palette.wall(), UPDATE_FLAGS);
        }
        if (random.nextInt(3) == 0) {
            level.setBlock(pose.at(x, z).above(height), Blocks.COPPER_BLOCK.defaultBlockState(), UPDATE_FLAGS);
        }
    }

    private static void orePile(WorldGenLevel level, Pose pose, int x, int z, RandomSource random) {
        BlockState[] ores = {
                Blocks.IRON_ORE.defaultBlockState(),
                Blocks.COPPER_ORE.defaultBlockState(),
                Blocks.REDSTONE_ORE.defaultBlockState(),
                Blocks.LAPIS_ORE.defaultBlockState()
        };
        int height = 1 + random.nextInt(2);
        for (int y = 0; y < height; y++) {
            level.setBlock(pose.at(x, z).above(y), ores[random.nextInt(ores.length)], UPDATE_FLAGS);
        }
    }

    private static void storehouse(WorldGenLevel level, Pose pose, Palette palette, RandomSource random) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(pose.at(dx, dz), Blocks.BARREL.defaultBlockState(), UPDATE_FLAGS);
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (random.nextInt(4) == 0) {
                    level.setBlock(pose.at(dx, dz).above(), palette.accent(), UPDATE_FLAGS);
                }
            }
        }
    }

    private static void gate(WorldGenLevel level, Pose pose, int x, int z, Palette palette, int height) {
        column(level, pose.at(x - 2, z), height, palette.wall());
        column(level, pose.at(x + 2, z), height, palette.wall());
        for (int dx = -2; dx <= 2; dx++) {
            level.setBlock(pose.at(x + dx, z).above(height), palette.roof(), UPDATE_FLAGS);
        }
        level.setBlock(pose.at(x, z).above(height - 1), palette.accent(), UPDATE_FLAGS);
    }

    private static void tower(WorldGenLevel level, Pose pose, int x, int z, int height, Palette palette) {
        for (int y = 0; y < height; y++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    boolean edge = dx != 0 || dz != 0;
                    BlockState state = edge ? palette.wall() : (y % 3 == 0 ? palette.accent() : palette.floor());
                    level.setBlock(pose.at(x + dx, z + dz).above(y), state, UPDATE_FLAGS);
                }
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlock(pose.at(x + dx, z + dz).above(height), palette.roof(), UPDATE_FLAGS);
            }
        }
    }

    private static void libraryWall(WorldGenLevel level, Pose pose, int x, int length, Palette palette) {
        for (int index = 0; index < length; index++) {
            level.setBlock(pose.at(x, -length / 2 + index), Blocks.BOOKSHELF.defaultBlockState(), UPDATE_FLAGS);
            if (index % 2 == 0) {
                level.setBlock(pose.at(x, -length / 2 + index).above(), palette.accent(), UPDATE_FLAGS);
            }
        }
    }

    private static void stoneCircle(WorldGenLevel level, Pose pose, int radius, Palette palette, RandomSource random) {
        for (int index = 0; index < 8; index++) {
            double angle = Math.PI * 2.0D * index / 8.0D;
            int x = (int) Math.round(Math.cos(angle) * radius);
            int z = (int) Math.round(Math.sin(angle) * radius);
            column(level, pose.at(x, z), 1 + random.nextInt(3), palette.wall());
        }
    }

    private static void garden(WorldGenLevel level, Pose pose, int x, int z, RandomSource random) {
        Block[] flowers = {Blocks.POPPY, Blocks.DANDELION, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET};
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (random.nextInt(3) == 0) {
                    level.setBlock(pose.at(x + dx, z + dz), Blocks.GRASS_BLOCK.defaultBlockState(), UPDATE_FLAGS);
                    level.setBlock(pose.at(x + dx, z + dz).above(),
                            flowers[random.nextInt(flowers.length)].defaultBlockState(), UPDATE_FLAGS);
                }
            }
        }
    }

    private static void livingArch(WorldGenLevel level, Pose pose, int x, int z) {
        for (int[] corner : new int[][]{{-3, 0}, {3, 0}}) {
            column(level, pose.at(x + corner[0], z + corner[1]), 4, Blocks.OAK_LOG.defaultBlockState());
        }
        for (int dx = -3; dx <= 3; dx++) {
            level.setBlock(pose.at(x + dx, z).above(4), Blocks.OAK_LEAVES.defaultBlockState(), UPDATE_FLAGS);
            if (Math.abs(dx) >= 2) {
                level.setBlock(pose.at(x + dx, z).above(3), Blocks.OAK_LEAVES.defaultBlockState(), UPDATE_FLAGS);
            }
        }
    }

    private static void songHall(WorldGenLevel level, Pose pose, Palette palette) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(pose.at(dx, dz), palette.floor(), UPDATE_FLAGS);
            }
        }
        for (int[] corner : new int[][]{{-3, -3}, {3, -3}, {-3, 3}, {3, 3}}) {
            column(level, pose.at(corner[0], corner[1]), 4, palette.post());
        }
        for (int dx = -2; dx <= 2; dx++) {
            level.setBlock(pose.at(dx, -3), Blocks.BOOKSHELF.defaultBlockState(), UPDATE_FLAGS);
        }
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(pose.at(dx, dz).above(4), palette.roof(), UPDATE_FLAGS);
            }
        }
    }

    private static void moonObservatory(WorldGenLevel level, Pose pose, RandomSource random) {
        for (int index = 0; index < 8; index++) {
            double angle = Math.PI * 2.0D * index / 8.0D;
            int x = (int) Math.round(Math.cos(angle) * 3.0D);
            int z = (int) Math.round(Math.sin(angle) * 3.0D);
            level.setBlock(pose.at(x, z), Blocks.AMETHYST_BLOCK.defaultBlockState(), UPDATE_FLAGS);
            if (random.nextInt(3) == 0) {
                level.setBlock(pose.at(x, z).above(), Blocks.AMETHYST_CLUSTER.defaultBlockState(), UPDATE_FLAGS);
            }
        }
    }

    private static void paths(WorldGenLevel level, Pose pose, Palette palette, RandomSource random) {
        BlockState path = random.nextBoolean()
                ? Blocks.GRAVEL.defaultBlockState()
                : Blocks.COARSE_DIRT.defaultBlockState();
        for (int index = -3; index <= 3; index++) {
            level.setBlock(pose.at(index, -4), path, UPDATE_FLAGS);
            level.setBlock(pose.at(0, index), path, UPDATE_FLAGS);
        }
        for (int index = 0; index < 3; index++) {
            level.setBlock(pose.at(-4 - index, -4), path, UPDATE_FLAGS);
            level.setBlock(pose.at(4 + index, 4), path, UPDATE_FLAGS);
        }
    }

    private static void perimeterFence(WorldGenLevel level, Pose pose, int radius, Palette palette,
                                       RandomSource random, int opening) {
        BlockState post = palette.post();
        for (int index = -radius; index <= radius; index++) {
            if (Math.abs(index) >= opening) {
                continue;
            }
            if (random.nextInt(3) == 0) {
                continue;
            }
            level.setBlock(pose.at(index, -radius), post, UPDATE_FLAGS);
            level.setBlock(pose.at(index, radius), post, UPDATE_FLAGS);
            level.setBlock(pose.at(-radius, index), post, UPDATE_FLAGS);
            level.setBlock(pose.at(radius, index), post, UPDATE_FLAGS);
        }
    }

    private static void ruin(WorldGenLevel level, Pose pose, Palette palette, RandomSource random, int radius) {
        level.setBlock(pose.at(0, 0), Blocks.COBWEB.defaultBlockState(), UPDATE_FLAGS);
        for (int index = 0; index < radius * 3; index++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            BlockState state = random.nextBoolean() ? palette.wall() : palette.floor();
            int height = 1 + random.nextInt(2);
            for (int y = 0; y < height; y++) {
                level.setBlock(pose.at(dx, dz).above(y), state, UPDATE_FLAGS);
            }
            if (random.nextInt(4) == 0) {
                level.setBlock(pose.at(dx, dz).above(height), Blocks.COBWEB.defaultBlockState(), UPDATE_FLAGS);
            }
        }
        for (int index = 0; index < 6; index++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            if (random.nextBoolean()) {
                level.setBlock(pose.at(dx, dz), Blocks.MOSS_BLOCK.defaultBlockState(), UPDATE_FLAGS);
            }
        }
    }

    private static void scatterGrowth(WorldGenLevel level, Pose pose, int radius, RandomSource random) {
        Block[] plants = {Blocks.SHORT_GRASS, Blocks.FERN, Blocks.POPPY, Blocks.DANDELION, Blocks.AZURE_BLUET,
                Blocks.OXEYE_DAISY, Blocks.CORNFLOWER};
        for (int index = 0; index < radius * 4; index++) {
            int dx = random.nextInt(radius * 4 + 1) - radius * 2;
            int dz = random.nextInt(radius * 4 + 1) - radius * 2;
            if (Math.abs(dx) <= radius && Math.abs(dz) <= radius) {
                continue;
            }
            BlockPos pos = pose.at(dx, dz);
            if (level.isEmptyBlock(pos) && level.getBlockState(pos.below()).isSolidRender()) {
                level.setBlock(pos, plants[random.nextInt(plants.length)].defaultBlockState(), UPDATE_FLAGS);
            }
        }
    }

    private static void fillFloor(WorldGenLevel level, Pose pose, int radius, BlockState state) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                level.setBlock(pose.at(x, z).below(), state, UPDATE_FLAGS);
            }
        }
    }

    private static void cornerLights(WorldGenLevel level, Pose pose, int radius, BlockState light) {
        for (int[] corner : new int[][]{{-radius, -radius}, {radius, -radius}, {-radius, radius}, {radius, radius}}) {
            column(level, pose.at(corner[0], corner[1]), 2, Blocks.DARK_OAK_FENCE.defaultBlockState());
            level.setBlock(pose.at(corner[0], corner[1]).above(2), light, UPDATE_FLAGS);
        }
    }

    private static void column(WorldGenLevel level, BlockPos start, int height, BlockState state) {
        for (int y = 0; y < height; y++) {
            level.setBlock(start.above(y), state, UPDATE_FLAGS);
        }
    }

    private static void line(WorldGenLevel level, Pose pose, int x, int z, int length, boolean alongX, BlockState state) {
        for (int index = 0; index < length; index++) {
            level.setBlock(alongX ? pose.at(x + index, z) : pose.at(x, z + index), state, UPDATE_FLAGS);
        }
    }

    private static void stock(WorldGenLevel level, BlockPos pos, RandomSource random, ItemStack... items) {
        level.setBlock(pos, Blocks.BARREL.defaultBlockState(), UPDATE_FLAGS);
        if (level.getBlockEntity(pos) instanceof Container container) {
            for (int slot = 0; slot < Math.min(container.getContainerSize(), items.length); slot++) {
                if (random.nextInt(4) != 0) {
                    container.setItem(slot, items[slot].copy());
                }
            }
        }
    }

    private static void placeBoard(WorldGenLevel level, BlockPos pos) {
        Block board = BuiltInRegistries.BLOCK.getValue(
                Identifier.fromNamespaceAndPath("earth_on_minecraft", "settlement_board"));
        if (board != null && board != Blocks.AIR) {
            level.setBlock(pos, board.defaultBlockState(), UPDATE_FLAGS);
        }
    }

    private record Pose(BlockPos center, int rotation) {
        BlockPos at(int x, int z) {
            int rotatedX = switch (rotation) {
                case 1 -> -z;
                case 2 -> -x;
                case 3 -> z;
                default -> x;
            };
            int rotatedZ = switch (rotation) {
                case 1 -> x;
                case 2 -> -z;
                case 3 -> -x;
                default -> z;
            };
            return center.offset(rotatedX, 0, rotatedZ);
        }
    }

    private record Palette(BlockState floor, BlockState wall, BlockState roof, BlockState post,
                           BlockState accent, BlockState light) {
        static Palette pick(Type type, RandomSource random) {
            return switch (type) {
                case WITCH_HAMLET -> pickWitch(random);
                case GOBLIN_EXCHANGE -> pickGoblin(random);
                case ACADEMY_OUTPOST -> pickAcademy(random);
                case DWARVEN_DELVING -> pickDwarven(random);
                case ELVEN_GROVE -> pickElven(random);
            };
        }

        private static Palette pickWitch(RandomSource random) {
            BlockState[] floors = {Blocks.MOSS_BLOCK.defaultBlockState(), Blocks.COARSE_DIRT.defaultBlockState(),
                    Blocks.PODZOL.defaultBlockState()};
            BlockState[] walls = {Blocks.DARK_OAK_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(),
                    Blocks.MANGROVE_PLANKS.defaultBlockState()};
            BlockState[] roofs = {Blocks.DARK_OAK_SLAB.defaultBlockState(), Blocks.MANGROVE_SLAB.defaultBlockState(),
                    Blocks.SPRUCE_SLAB.defaultBlockState()};
            return new Palette(pick(floors, random), pick(walls, random), pick(roofs, random),
                    Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState(), Blocks.RED_MUSHROOM_BLOCK.defaultBlockState(),
                    Blocks.SOUL_LANTERN.defaultBlockState());
        }

        private static Palette pickGoblin(RandomSource random) {
            BlockState[] floors = {Blocks.MUD_BRICKS.defaultBlockState(), Blocks.PACKED_MUD.defaultBlockState(),
                    Blocks.CRACKED_STONE_BRICKS.defaultBlockState()};
            BlockState[] walls = {Blocks.MUD_BRICKS.defaultBlockState(), Blocks.TERRACOTTA.defaultBlockState(),
                    Blocks.COPPER_BLOCK.defaultBlockState()};
            BlockState[] roofs = {Blocks.ORANGE_WOOL.defaultBlockState(), Blocks.GRAY_WOOL.defaultBlockState(),
                    Blocks.BROWN_WOOL.defaultBlockState()};
            return new Palette(pick(floors, random), pick(walls, random), pick(roofs, random),
                    Blocks.COPPER_GRATE.defaultBlockState(), Blocks.COPPER_BLOCK.defaultBlockState(),
                    Blocks.LANTERN.defaultBlockState());
        }

        private static Palette pickAcademy(RandomSource random) {
            BlockState[] floors = {Blocks.SMOOTH_STONE.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(),
                    Blocks.DEEPSLATE_TILES.defaultBlockState()};
            BlockState[] walls = {Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(),
                    Blocks.STONE_BRICK_WALL.defaultBlockState(), Blocks.CHISELED_DEEPSLATE.defaultBlockState()};
            BlockState[] roofs = {Blocks.BLUE_WOOL.defaultBlockState(), Blocks.CYAN_WOOL.defaultBlockState(),
                    Blocks.LIGHT_BLUE_WOOL.defaultBlockState()};
            return new Palette(pick(floors, random), pick(walls, random), pick(roofs, random),
                    Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), Blocks.AMETHYST_BLOCK.defaultBlockState(),
                    Blocks.LANTERN.defaultBlockState());
        }

        private static Palette pickDwarven(RandomSource random) {
            BlockState[] floors = {Blocks.DEEPSLATE_TILES.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(),
                    Blocks.POLISHED_DEEPSLATE.defaultBlockState()};
            BlockState[] walls = {Blocks.POLISHED_DEEPSLATE.defaultBlockState(),
                    Blocks.DEEPSLATE_BRICKS.defaultBlockState(), Blocks.CHISELED_STONE_BRICKS.defaultBlockState()};
            BlockState[] roofs = {Blocks.DEEPSLATE_TILE_SLAB.defaultBlockState(),
                    Blocks.STONE_BRICK_SLAB.defaultBlockState(), Blocks.COBBLED_DEEPSLATE_SLAB.defaultBlockState()};
            return new Palette(pick(floors, random), pick(walls, random), pick(roofs, random),
                    Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState(), Blocks.CRYING_OBSIDIAN.defaultBlockState(),
                    Blocks.SOUL_LANTERN.defaultBlockState());
        }

        private static Palette pickElven(RandomSource random) {
            BlockState[] floors = {Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.MOSS_BLOCK.defaultBlockState(),
                    Blocks.OAK_PLANKS.defaultBlockState()};
            BlockState[] walls = {Blocks.OAK_LOG.defaultBlockState(), Blocks.BIRCH_LOG.defaultBlockState(),
                    Blocks.STRIPPED_OAK_LOG.defaultBlockState()};
            BlockState[] roofs = {Blocks.OAK_LEAVES.defaultBlockState(), Blocks.BIRCH_LEAVES.defaultBlockState(),
                    Blocks.AZALEA_LEAVES.defaultBlockState()};
            return new Palette(pick(floors, random), pick(walls, random), pick(roofs, random),
                    Blocks.OAK_LOG.defaultBlockState(), Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(),
                    Blocks.LANTERN.defaultBlockState());
        }

        private static BlockState pick(BlockState[] states, RandomSource random) {
            return states[random.nextInt(states.length)];
        }
    }

    public enum Type {
        WITCH_HAMLET("witch_hamlet", 4),
        GOBLIN_EXCHANGE("goblin_exchange", 4),
        ACADEMY_OUTPOST("academy_outpost", 5),
        DWARVEN_DELVING("dwarven_delving", 5),
        ELVEN_GROVE("elven_grove", 5);

        private final String id;
        private final int radius;

        Type(String id, int radius) {
            this.id = id;
            this.radius = radius;
        }

        public String id() {
            return id;
        }

        public int radius() {
            return radius;
        }

        public static Type byId(String id) {
            for (Type type : values()) {
                if (type.id.equals(id)) {
                    return type;
                }
            }
            return WITCH_HAMLET;
        }
    }

    /** Kept for the anchor block entity, which only needs the configured type. */
    static List<Type> types() {
        return List.of(Type.values());
    }
}
