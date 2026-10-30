package com.xxsx.fantasyearth;

/**
 * Declared inputs of a magic graph. A crystal and the player's mana are separate
 * declarations; neither is inserted into the pipeline as if it were a converter node.
 */
public enum SpellSource {
    PLAYER_MANA("mana", "control", "energy"),
    AETHER_CRYSTAL("crystal", "energy"),
    LOCAL_AETHER("field", "energy");

    private final String role;
    private final java.util.List<String> outputPorts;

    SpellSource(String role, String... outputPorts) {
        this.role = role;
        this.outputPorts = java.util.List.of(outputPorts);
    }

    public String role() {
        return role;
    }

    public java.util.List<String> outputPorts() {
        return outputPorts;
    }

    public boolean feeds(String port) {
        return outputPorts.contains(port);
    }

    public String translationKey() {
        return "spell_source.fantasy_earth." + name().toLowerCase();
    }
}
