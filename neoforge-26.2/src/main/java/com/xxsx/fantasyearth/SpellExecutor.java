package com.xxsx.fantasyearth;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server-authoritative planning for fantasy spell graphs.
 *
 * <p>Planning is pure: it reads the graph, the player's route progress, cooldown and
 * mana, and returns a plan in which AEU and MP remain separately labelled. Commit is
 * done by {@link ArcaneSkill} after the effect itself succeeds.</p>
 */
public final class SpellExecutor {
    private SpellExecutor() {
    }

    public static Plan plan(ServerPlayer player, ServerLevel level, ArcaneFocus focus) {
        SpellGraph graph = SpellCatalog.resolve(focus);
        SpellGraph.Validation validation = graph.validate();
        if (!validation.valid()) {
            return Plan.reject("invalid_graph", validation.errorCode() + ": " + validation.message());
        }
        if (!focus.isUnlocked(player)) {
            return Plan.reject("requires_research", "the selected focus is not researched");
        }
        long remaining = ArcanaPower.getSkillCooldownTicks(player, level);
        if (remaining > 0L) {
            return Plan.reject("cooldown", "skill is cooling down for " + ((remaining + 19L) / 20L) + "s");
        }
        double aeuCost = graph.energyCostAeu();
        if (aeuCost > 0.0D) {
            AetherChunkField.Reading reading = AetherChunkField.read(level, player.blockPosition());
            double available = AetherLedger.availableAeu(reading.value(), reading.disturbance());
            if (available + 0.0001D < aeuCost) {
                return Plan.reject("field_low",
                        "local aether field holds " + Math.round(available) + " AEU, " + Math.round(aeuCost) + " needed");
            }
        }
        double manaCost = graph.manaCost();
        if (!ArcanaPower.canSpendMana(player, manaCost)) {
            return Plan.reject("mana_low", "at least " + ArcanaPower.format(manaCost) + " mana is required");
        }
        return Plan.ok(graph, aeuCost, manaCost);
    }

    public record Plan(boolean accepted, SpellGraph graph, double aeuCost, double manaCost,
                       String errorCode, String message) {
        public static Plan ok(SpellGraph graph, double aeuCost, double manaCost) {
            return new Plan(true, graph, aeuCost, manaCost, "", "");
        }

        public static Plan reject(String code, String message) {
            return new Plan(false, null, 0.0D, 0.0D, code, message);
        }
    }
}
