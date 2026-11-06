package com.xxsx.fantasyearth;

public final class ArcanaBalance {
    private ArcanaBalance() {
    }

    static double exhaustionReduction(double enduranceBonus) {
        return clamp(nonNegative(enduranceBonus) * 0.24D, 0.0D, 0.82D);
    }

    static double combatDamageReduction(double bodyTemperingBonus) {
        return clamp(nonNegative(bodyTemperingBonus) * 0.11D, 0.0D, 0.30D);
    }

    static double breathMultiplier(double breathCapacityBonus) {
        return 1.0D + Math.min(3.0D, nonNegative(breathCapacityBonus) / 300.0D);
    }

    static double fallDamageReduction(double enduranceBonus) {
        return Math.min(0.45D, nonNegative(enduranceBonus) * 0.14D);
    }

    static int xpNeededForLevel(int level, int maxLevel) {
        if (level <= 0 || level >= maxLevel) {
            return 0;
        }
        return 60 + level * 35 + level * level * 8;
    }

    public static int computeAlchemyTemperature(int fieldVal) {
        int clamped = Math.max(0, Math.min(100, fieldVal));
        return Math.max(300, Math.min(1300, 300 + 10 * clamped));
    }

    public static boolean canContractWithAffinity(int affinity) {
        return affinity >= 30;
    }

    static double clamp(double value, double min, double max) {
        if (Double.isNaN(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    private static double nonNegative(double value) {
        return Double.isFinite(value) && value > 0.0D ? value : 0.0D;
    }
}
