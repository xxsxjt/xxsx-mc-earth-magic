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
    private static final Map<String, SpellGraph> BY_ID = Map.of(
            WARD.id(), WARD,
            ATTUNEMENT.id(), ATTUNEMENT,
            BREATH_WARD.id(), BREATH_WARD);

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
