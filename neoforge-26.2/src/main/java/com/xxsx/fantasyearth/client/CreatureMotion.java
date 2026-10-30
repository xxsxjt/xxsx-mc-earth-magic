package com.xxsx.fantasyearth.client;

import net.minecraft.util.Mth;

/**
 * Small shared motion vocabulary for the mod's creatures.
 *
 * <p>The point is not physical simulation: it is to give every creature a
 * readable idle, gait and attack beat that differs from vanilla mobs.</p>
 */
public final class CreatureMotion {
    private CreatureMotion() {
    }

    public static float breathe(float age, float rate) {
        return Mth.sin(age * rate);
    }

    public static float wave(float age, float rate, float phase) {
        return Mth.sin(age * rate + phase);
    }

    public static float gait(float walk, float phase, float amplitude, float speed) {
        return Mth.cos(walk * 0.6662F + phase) * amplitude * speed;
    }

    public static float clampLook(float radians, float limit) {
        return Mth.clamp(radians, -limit, limit);
    }

    /** Sharp, short flick used for ears, fins and antennae. */
    public static float twitch(float age, float rate, float phase, float amplitude) {
        float value = Mth.sin(age * rate + phase);
        return value * value * value * amplitude;
    }

    /** 0 to 1 ramp during the first third of an attack swing. */
    public static float windUp(float progress) {
        return Mth.sin(Mth.clamp(progress / 0.32F, 0.0F, 1.0F) * Mth.HALF_PI);
    }

    /** 0 to 1 ramp through the actual strike. */
    public static float strike(float progress) {
        return Mth.clamp((progress - 0.32F) / 0.40F, 0.0F, 1.0F);
    }

    /** 0 to 1 ramp while returning to the idle pose. */
    public static float recover(float progress) {
        return Mth.clamp((progress - 0.72F) / 0.28F, 0.0F, 1.0F);
    }

    public static float idleBlend(float walkSpeed) {
        return Mth.clamp(1.0F - walkSpeed * 2.2F, 0.0F, 1.0F);
    }
}
