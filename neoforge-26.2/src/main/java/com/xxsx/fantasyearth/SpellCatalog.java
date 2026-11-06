package com.xxsx.fantasyearth;

import java.util.List;
import java.util.Map;

/** Built-in magic graphs; data-driven expansion must reference these stable ids. */
public final class SpellCatalog {
    public static final SpellGraph WARD = new SpellGraph("fantasy_earth:arcane_ward",
            List.of(SpellSource.PLAYER_MANA, SpellSource.LOCAL_AETHER),
            List.of(
                    node(SpellNode.ARCANE_FOCUS, "energy", 0.0D, 0.0D),
                    node(SpellNode.WARD_SHAPER, "energy", 0.0D, 0.0D),
                    node(SpellNode.SELF_TARGET, "control", 0.0D, 0.0D),
                    node(SpellNode.WARD_EFFECT, "target", 60.0D, 12.0D),
                    node(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)
            ));
    public static final SpellGraph ATTUNEMENT = new SpellGraph("fantasy_earth:attunement",
            List.of(SpellSource.PLAYER_MANA, SpellSource.LOCAL_AETHER),
            List.of(
                    node(SpellNode.ARCANE_FOCUS, "energy", 0.0D, 0.0D),
                    node(SpellNode.WARD_SHAPER, "energy", 0.0D, 0.0D),
                    node(SpellNode.SELF_TARGET, "control", 0.0D, 0.0D),
                    node(SpellNode.WARD_EFFECT, "target", 45.0D, 10.0D),
                    node(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)
            ));
    public static final SpellGraph BREATH_WARD = new SpellGraph("fantasy_earth:breath_ward",
            List.of(SpellSource.PLAYER_MANA, SpellSource.LOCAL_AETHER),
            List.of(
                    node(SpellNode.ARCANE_FOCUS, "energy", 0.0D, 0.0D),
                    node(SpellNode.WARD_SHAPER, "energy", 0.0D, 0.0D),
                    node(SpellNode.SELF_TARGET, "control", 0.0D, 0.0D),
                    node(SpellNode.WARD_EFFECT, "target", 40.0D, 10.0D),
                    node(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)
            ));
    public static final SpellGraph EVOCATION = new SpellGraph("fantasy_earth:evocation",
            List.of(SpellSource.PLAYER_MANA, SpellSource.LOCAL_AETHER),
            List.of(
                    node(SpellNode.ARCANE_FOCUS, "energy", 0.0D, 0.0D),
                    node(SpellNode.WARD_SHAPER, "energy", 0.0D, 0.0D),
                    node(SpellNode.SELF_TARGET, "control", 0.0D, 0.0D),
                    node(SpellNode.WARD_EFFECT, "target", 0.0D, 14.0D),
                    node(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)
            ));
    public static final SpellGraph ILLUSION = new SpellGraph("fantasy_earth:illusion",
            List.of(SpellSource.PLAYER_MANA, SpellSource.LOCAL_AETHER),
            List.of(
                    node(SpellNode.ARCANE_FOCUS, "energy", 0.0D, 0.0D),
                    node(SpellNode.WARD_SHAPER, "energy", 0.0D, 0.0D),
                    node(SpellNode.SELF_TARGET, "control", 0.0D, 0.0D),
                    node(SpellNode.WARD_EFFECT, "target", 0.0D, 16.0D),
                    node(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)
            ));
    public static final SpellGraph TRANSLOCATION = new SpellGraph("fantasy_earth:translocation",
            List.of(SpellSource.PLAYER_MANA, SpellSource.LOCAL_AETHER),
            List.of(
                    node(SpellNode.ARCANE_FOCUS, "energy", 0.0D, 0.0D),
                    node(SpellNode.WARD_SHAPER, "energy", 0.0D, 0.0D),
                    node(SpellNode.SELF_TARGET, "control", 0.0D, 0.0D),
                    node(SpellNode.WARD_EFFECT, "target", 0.0D, 18.0D),
                    node(SpellNode.AETHER_DISTURBANCE_SINK, "target", 0.0D, 0.0D)
            ));
    private static final Map<String, SpellGraph> BY_ID = Map.of(
            WARD.id(), WARD,
            ATTUNEMENT.id(), ATTUNEMENT,
            BREATH_WARD.id(), BREATH_WARD,
            EVOCATION.id(), EVOCATION,
            ILLUSION.id(), ILLUSION,
            TRANSLOCATION.id(), TRANSLOCATION);

    private SpellCatalog() {
    }

    public static Map<String, SpellGraph> graphs() {
        return BY_ID;
    }

    public static SpellGraph ward() {
        return WARD;
    }

    public static SpellGraph forFocus(ArcaneFocus focus) {
        return switch (focus) {
            case ATTUNEMENT -> ATTUNEMENT;
            case BODY_WARD -> WARD;
            case BREATH_WARD -> BREATH_WARD;
            case EVOCATION -> EVOCATION;
            case ILLUSION -> ILLUSION;
            case TRANSLOCATION -> TRANSLOCATION;
        };
    }

    public static ArcaneFocus focusForGraphId(String graphId) {
        if (graphId == null) {
            return ArcaneFocus.ATTUNEMENT;
        }
        return switch (graphId) {
            case "fantasy_earth:arcane_ward", "arcane_ward", "body_ward" -> ArcaneFocus.BODY_WARD;
            case "fantasy_earth:breath_ward", "breath_ward" -> ArcaneFocus.BREATH_WARD;
            case "fantasy_earth:evocation", "evocation" -> ArcaneFocus.EVOCATION;
            case "fantasy_earth:illusion", "illusion" -> ArcaneFocus.ILLUSION;
            case "fantasy_earth:translocation", "translocation" -> ArcaneFocus.TRANSLOCATION;
            default -> ArcaneFocus.ATTUNEMENT;
        };
    }

    /** Data overrides win, but the built-in graph remains the offline fallback. */
    public static SpellGraph resolve(ArcaneFocus focus) {
        SpellGraph data = SpellGraphLoader.find(forFocus(focus).id());
        return data != null ? data : forFocus(focus);
    }

    private static SpellGraph.SpellNodeInstance node(SpellNode kind, String input, double costAeu, double costMana) {
        return new SpellGraph.SpellNodeInstance(kind, input, costAeu, costMana);
    }
}
