package com.xxsx.fantasyearth;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

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
        return plan(player, level, focus, false);
    }

    public static Plan plan(ServerPlayer player, ServerLevel level, ArcaneFocus focus, boolean fromScroll) {
        SpellGraph graph = SpellCatalog.resolve(focus);
        SpellGraph.Validation validation = graph.validate();
        if (!validation.valid()) {
            return Plan.reject("invalid_graph", validation.errorCode() + ": " + validation.message());
        }
        if (!focus.isUnlocked(player)) {
            return Plan.reject("lore_locked", "the selected school is locked by knowledge prerequisite");
        }
        if (!fromScroll) {
            String runeCheck = checkFocusRuneRequirement(player, focus);
            if (!runeCheck.isEmpty()) {
                return Plan.reject(runeCheck, "focus rune requirement not met for " + focus.path());
            }
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

    /** F-SPELL-01: evocation requires rune_power >= 2, illusion requires rune_flow >= 2, translocation requires rune_flow >= 3 in held arcane_focus. */
    public static String checkFocusRuneRequirement(ServerPlayer player, ArcaneFocus focus) {
        String requiredKind = switch (focus) {
            case EVOCATION -> "power";
            case ILLUSION, TRANSLOCATION -> "flow";
            default -> "";
        };
        int requiredTier = switch (focus) {
            case EVOCATION, ILLUSION -> 2;
            case TRANSLOCATION -> 3;
            default -> 0;
        };
        if (requiredTier <= 0) {
            return "";
        }
        ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
        ItemStack focusStack = main.is(FantasyEarth.ARCANE_FOCUS_ITEM.get()) ? main
                : (off.is(FantasyEarth.ARCANE_FOCUS_ITEM.get()) ? off : ItemStack.EMPTY);
        if (focusStack.isEmpty()) {
            return "missing_focus";
        }
        ArcaneFocusComponent comp = ArcaneFocusComponent.read(focusStack);
        if (comp.maxRuneTier(requiredKind) < requiredTier) {
            return "rune_tier_low";
        }
        return "";
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
