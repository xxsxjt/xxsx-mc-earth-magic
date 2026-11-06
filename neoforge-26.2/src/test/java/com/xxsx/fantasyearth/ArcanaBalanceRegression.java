package com.xxsx.fantasyearth;

public final class ArcanaBalanceRegression {
    private static final double EPSILON = 0.000001D;

    private ArcanaBalanceRegression() {
    }

    public static void main(String[] args) {
        verifyMonotonicAndCapped();
        verifyInvalidValuesAreBounded();
        verifyExperienceCurve();
        verifyJourneyMask();
        verifySharedEnergyContract();
        verifySpellGraphs();
    }

    private static void verifyMonotonicAndCapped() {
        double previousExhaustion = 0.0D;
        double previousCombat = 0.0D;
        double previousBreath = 1.0D;
        double previousFall = 0.0D;
        for (int level = 0; level <= 100; level++) {
            double exhaustion = ArcanaBalance.exhaustionReduction(level * 0.10D);
            double combat = ArcanaBalance.combatDamageReduction(level * 0.09D);
            double breath = ArcanaBalance.breathMultiplier(level * 30.0D);
            double fall = ArcanaBalance.fallDamageReduction(level * 0.08D);
            require(exhaustion + EPSILON >= previousExhaustion, "exhaustion reduction regressed");
            require(combat + EPSILON >= previousCombat, "combat reduction regressed");
            require(breath + EPSILON >= previousBreath, "breath multiplier regressed");
            require(fall + EPSILON >= previousFall, "fall reduction regressed");
            previousExhaustion = exhaustion;
            previousCombat = combat;
            previousBreath = breath;
            previousFall = fall;
        }
        close(0.82D, ArcanaBalance.exhaustionReduction(1000.0D));
        close(0.30D, ArcanaBalance.combatDamageReduction(1000.0D));
        close(4.0D, ArcanaBalance.breathMultiplier(100000.0D));
        close(0.45D, ArcanaBalance.fallDamageReduction(1000.0D));
    }

    private static void verifyInvalidValuesAreBounded() {
        close(0.0D, ArcanaBalance.exhaustionReduction(Double.NaN));
        close(0.0D, ArcanaBalance.combatDamageReduction(Double.NEGATIVE_INFINITY));
        close(1.0D, ArcanaBalance.breathMultiplier(Double.POSITIVE_INFINITY));
        close(0.0D, ArcanaBalance.fallDamageReduction(-1.0D));
    }

    private static void verifyExperienceCurve() {
        int previous = 0;
        for (int level = 1; level < 10; level++) {
            int needed = ArcanaBalance.xpNeededForLevel(level, 10);
            require(needed > previous, "experience curve must increase");
            previous = needed;
        }
        require(ArcanaBalance.xpNeededForLevel(0, 10) == 0, "locked route must not require experience");
        require(ArcanaBalance.xpNeededForLevel(10, 10) == 0, "capped route must not require experience");
    }

    private static void verifyJourneyMask() {
        int total = 6;
        int mask = 0;
        for (int milestoneId = 0; milestoneId < total; milestoneId++) {
            require(JourneyProgress.nextId(mask, total) == milestoneId, "journey order changed");
            int next = JourneyProgress.apply(mask, milestoneId, total);
            require(JourneyProgress.count(next, total) == JourneyProgress.count(mask, total) + 1,
                    "journey count did not advance");
            require(JourneyProgress.apply(next, milestoneId, total) == next,
                    "journey milestone is not idempotent");
            mask = next;
        }
        require(JourneyProgress.isComplete(mask, total), "journey full mask is incomplete");
        require(JourneyProgress.nextId(mask, total) == -1, "complete journey still has a next step");
        require(JourneyProgress.count(mask, total) == total, "journey total mismatch");
        require(JourneyProgress.sanitize(mask | (1 << 20), total) == mask,
                "journey mask kept unknown bits");
    }

    private static void verifySharedEnergyContract() {
        close(0.0D, AetherLedger.clampField(Double.NaN));
        close(100.0D, AetherLedger.clampField(99999.0D));
        close(0.0D, AetherLedger.clampDisturbance(-5.0D));
        close(45.0D, AetherLedger.clampDisturbance(99999.0D));
        close(100.0D, AetherLedger.extractableField(100.0D, 0.0D));
        close(77.5D, AetherLedger.extractableField(100.0D, 45.0D));
        close(0.0D, AetherLedger.extractableField(0.0D, 0.0D));
        close(0.9D, AetherLedger.manaValueOfAeu(10.0D));
        close(111.111111D, AetherLedger.aeuCostOfMana(10.0D));
        require(AetherLedger.CONTRACT_VERSION == 1, "contract version changed without migration");
        require(AetherLedger.report(100.0D, 45.0D).extractableField() > 0.0D,
                "dense field must still provide flux after disturbance");
        require(AetherLedger.report(0.0D, 45.0D).isDepleted(), "empty field must report depletion");
        close(1000.0D, AetherLedger.availableAeu(100.0D, 0.0D));
        close(775.0D, AetherLedger.availableAeu(100.0D, 45.0D));
        close(4.2D, AetherLedger.disturbanceForAeu(60.0D));
    }

    private static void verifySpellGraphs() {
        require(SpellCatalog.graphs().size() == 6, "magic graph catalog changed without a test update");
        SpellGraph ward = SpellCatalog.ward();
        SpellGraph.Validation validation = ward.validate();
        require(validation.valid(), "arcane ward graph invalid: " + validation.errorCode() + " " + validation.message());
        close(12.0D, ward.manaCost());
        close(60.0D, ward.energyCostAeu());
        require(ward.requires(SpellSource.PLAYER_MANA), "ward graph lost its player mana source");
        require(ward.requires(SpellSource.LOCAL_AETHER), "ward graph lost its local aether source");
        require(!ward.requires(SpellSource.AETHER_CRYSTAL),
                "the ward graph must not declare a crystal it does not consume");
        for (SpellGraph graph : SpellCatalog.graphs().values()) {
            require(graph.validate().valid(), "built-in magic graph invalid: " + graph.id());
            require(graph.energyCostAeu() + graph.manaCost() > 0.0D,
                    "magic graph must not be free: " + graph.id());
        }
        SpellGraph badPort = new SpellGraph("fantasy_earth:bad_port", java.util.List.of(
                SpellSource.PLAYER_MANA),
                java.util.List.of(
                        new SpellGraph.SpellNodeInstance(SpellNode.WARD_EFFECT, "energy", 0.0D, 10.0D),
                        new SpellGraph.SpellNodeInstance(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)));
        require(!badPort.validate().valid(), "ward effect accepted an energy port");
        require("invalid_input_port".equals(badPort.validate().errorCode()), "bad magic port produced wrong error");
        SpellGraph noSink = new SpellGraph("fantasy_earth:no_sink", java.util.List.of(
                SpellSource.PLAYER_MANA),
                java.util.List.of(
                        new SpellGraph.SpellNodeInstance(SpellNode.WARD_EFFECT, "target", 0.0D, 10.0D)));
        require(!noSink.validate().valid(), "graph without aether sink was accepted");
        SpellGraph free = new SpellGraph("fantasy_earth:free", java.util.List.of(
                SpellSource.PLAYER_MANA),
                java.util.List.of(
                        new SpellGraph.SpellNodeInstance(SpellNode.SELF_TARGET, "control", 0.0D, 0.0D),
                        new SpellGraph.SpellNodeInstance(SpellNode.WARD_EFFECT, "target", 0.0D, 0.0D),
                        new SpellGraph.SpellNodeInstance(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)));
        require("free_spell".equals(free.validate().errorCode()), "zero-cost magic graph was accepted");
        SpellGraph unbounded = new SpellGraph("fantasy_earth:unbounded", java.util.List.of(
                SpellSource.PLAYER_MANA),
                java.util.List.of(
                        new SpellGraph.SpellNodeInstance(SpellNode.ARCANE_FOCUS, "energy", 0.0D, 0.0D),
                        new SpellGraph.SpellNodeInstance(SpellNode.WARD_SHAPER, "energy", 0.0D, 0.0D),
                        new SpellGraph.SpellNodeInstance(SpellNode.SELF_TARGET, "control", 0.0D, 0.0D),
                        new SpellGraph.SpellNodeInstance(SpellNode.WARD_EFFECT, "target", 25.0D, 10.0D),
                        new SpellGraph.SpellNodeInstance(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)));
        require("missing_aether_source".equals(unbounded.validate().errorCode()),
                "AEU cost without an aether source was accepted");
    }

    private static void close(double expected, double actual) {
        require(Math.abs(expected - actual) <= EPSILON, "expected " + expected + ", got " + actual);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
