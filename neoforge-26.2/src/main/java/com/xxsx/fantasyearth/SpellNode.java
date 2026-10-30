package com.xxsx.fantasyearth;

import java.util.Set;

/** Whitelisted pipeline nodes. Energy sources are declared separately in {@link SpellSource}. */
public enum SpellNode {
    ARCANE_FOCUS("converter", "energy", Set.of("energy")),
    WARD_SHAPER("shaper", "control", Set.of("energy")),
    SELF_TARGET("target", "target", Set.of("control")),
    WARD_EFFECT("effect", "target", Set.of("target")),
    AETHER_DISTURBANCE_SINK("sink", "none", Set.of("energy", "target"));

    private final String role;
    private final String outputPort;
    private final Set<String> allowedInputPorts;

    SpellNode(String role, String outputPort, Set<String> allowedInputPorts) {
        this.role = role;
        this.outputPort = outputPort;
        this.allowedInputPorts = allowedInputPorts;
    }

    public String role() {
        return role;
    }

    public String outputPort() {
        return outputPort;
    }

    public boolean acceptsInput(String port) {
        return allowedInputPorts.contains(port);
    }

    public boolean isSink() {
        return "sink".equals(role);
    }

    public boolean isEffect() {
        return this == WARD_EFFECT;
    }

    public String translationKey() {
        return "spell_node.fantasy_earth." + name().toLowerCase();
    }
}
