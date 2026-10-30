package com.xxsx.fantasyearth;

/**
 * Shared invariant values and arithmetic for the arcane energy contract.
 *
 * <p>This class deliberately is not a gameplay registry and holds no player state.
 * It encodes the units agreed with Xuanhuan Earth so both sides can be tested against
 * the same numbers without a runtime dependency.</p>
 */
public final class AetherLedger {
    public static final int CONTRACT_VERSION = 1;
    public static final double AEU_PER_KILOJOULE = 1.0D;
    public static final double AEU_PER_FIELD_LEVEL = 10.0D;
    public static final double AEU_PER_MANA = 10.0D;
    public static final double CAPTURE_EFFICIENCY = 0.80D;
    public static final double RELEASE_EFFICIENCY = 0.90D;
    public static final double FIELD_MIN = 0.0D;
    public static final double FIELD_MAX = 100.0D;
    public static final double DISTURBANCE_MAX = 45.0D;
    private static final double EPSILON = 0.000001D;

    private AetherLedger() {
    }

    public static double clampField(double value) {
        if (!Double.isFinite(value)) {
            return FIELD_MIN;
        }
        return Math.max(FIELD_MIN, Math.min(FIELD_MAX, value));
    }

    public static double clampDisturbance(double value) {
        if (!Double.isFinite(value)) {
            return 0.0D;
        }
        return Math.max(0.0D, Math.min(DISTURBANCE_MAX, value));
    }

    public static double extractableField(double fieldLevel, double disturbance) {
        return Math.max(0.0D, clampField(fieldLevel) - clampDisturbance(disturbance) * 0.5D);
    }

    public static double availableAeu(double fieldLevel, double disturbance) {
        return extractableField(fieldLevel, disturbance) * AEU_PER_FIELD_LEVEL;
    }

    public static double disturbanceForAeu(double aeu) {
        return Math.max(0.0D, aeu) * 0.07D;
    }

    public static double manaValueOfAeu(double aeu) {
        return Math.max(0.0D, aeu) * RELEASE_EFFICIENCY / AEU_PER_MANA;
    }

    public static double aeuCostOfMana(double mana) {
        return Math.max(0.0D, mana) * AEU_PER_MANA / RELEASE_EFFICIENCY;
    }

    public static Report report(double fieldLevel, double disturbance) {
        return new Report(clampField(fieldLevel), clampDisturbance(disturbance),
                extractableField(fieldLevel, disturbance));
    }

    public record Report(double fieldLevel, double disturbance, double extractableField) {
        public boolean isDepleted() {
            return extractableField <= EPSILON;
        }
    }
}
