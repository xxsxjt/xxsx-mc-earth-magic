package com.xxsx.fantasyearth;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Server-side helpers for the mod's own particles.
 *
 * <p>Vanilla particle ids are still used where they read better (smoke, bubbles),
 * but every arcane action now carries at least one Fantasy Earth sprite.</p>
 */
public final class ArcaneVfx {
    private ArcaneVfx() {
    }

    public static void moteBurst(ServerLevel level, double x, double y, double z, int count, double spread) {
        level.sendParticles(FantasyEarthParticles.AETHER_MOTE.get(), x, y, z, count,
                spread, spread * 0.7D, spread, 0.01D);
    }

    public static void sparkBurst(ServerLevel level, double x, double y, double z, int count, double spread) {
        level.sendParticles(FantasyEarthParticles.RUNE_SPARK.get(), x, y, z, count,
                spread, spread * 0.6D, spread, 0.06D);
    }

    public static void shardBurst(ServerLevel level, double x, double y, double z, int count, double spread) {
        level.sendParticles(FantasyEarthParticles.CRYSTAL_SHARD.get(), x, y, z, count,
                spread, spread * 0.8D, spread, 0.04D);
    }

    public static void runeRing(ServerLevel level, Vec3 center, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double angle = Mth.TWO_PI * i / count;
            level.sendParticles(FantasyEarthParticles.WARD_RUNE.get(),
                    center.x + Math.cos(angle) * radius,
                    center.y + 0.08D,
                    center.z + Math.sin(angle) * radius,
                    1, 0.02D, 0.02D, 0.02D, 0.0D);
        }
    }

    public static void runeRingAround(ServerLevel level, Vec3 center, double radius, int count, double height) {
        for (int i = 0; i < count; i++) {
            double angle = Mth.TWO_PI * i / count;
            level.sendParticles(FantasyEarthParticles.WARD_RUNE.get(),
                    center.x + Math.cos(angle) * radius,
                    center.y + height,
                    center.z + Math.sin(angle) * radius,
                    1, 0.0D, 0.03D, 0.0D, 0.0D);
        }
    }

    public static void focusCast(ServerLevel level, Vec3 at, ArcaneFocus focus) {
        switch (focus) {
            case ATTUNEMENT, EVOCATION -> {
                moteBurst(level, at.x, at.y + 0.9D, at.z, 26, 0.7D);
                runeRingAround(level, at, 1.05D, 10, 0.25D);
            }
            case BODY_WARD, ILLUSION -> {
                sparkBurst(level, at.x, at.y + 0.8D, at.z, 22, 0.55D);
                runeRingAround(level, at, 0.85D, 8, 1.05D);
            }
            case BREATH_WARD, TRANSLOCATION -> {
                moteBurst(level, at.x, at.y + 1.1D, at.z, 20, 0.45D);
                shardBurst(level, at.x, at.y + 0.6D, at.z, 8, 0.4D);
            }
        }
    }
}
