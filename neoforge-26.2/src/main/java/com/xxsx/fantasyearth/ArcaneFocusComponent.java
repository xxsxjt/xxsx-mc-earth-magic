package com.xxsx.fantasyearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

/** F-047, F-RUNE-02..04 Arcane Focus component: 3 rune slots, 3 gem slots, jade housing, durability. */
public record ArcaneFocusComponent(String rune0, String rune1, String rune2,
                                   String gem0, String gem1, String gem2,
                                   boolean jadeHousing, int durability) {
    public static final int BASE_MAX_DURABILITY = 500;
    public static final int JADE_MAX_DURABILITY = 600;

    public static final ArcaneFocusComponent DEFAULT = new ArcaneFocusComponent(
            "", "", "", "", "", "", false, BASE_MAX_DURABILITY);

    public static final Codec<ArcaneFocusComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.string(0, 32).optionalFieldOf("rune_0", "").forGetter(ArcaneFocusComponent::rune0),
            Codec.string(0, 32).optionalFieldOf("rune_1", "").forGetter(ArcaneFocusComponent::rune1),
            Codec.string(0, 32).optionalFieldOf("rune_2", "").forGetter(ArcaneFocusComponent::rune2),
            Codec.string(0, 32).optionalFieldOf("gem_0", "").forGetter(ArcaneFocusComponent::gem0),
            Codec.string(0, 32).optionalFieldOf("gem_1", "").forGetter(ArcaneFocusComponent::gem1),
            Codec.string(0, 32).optionalFieldOf("gem_2", "").forGetter(ArcaneFocusComponent::gem2),
            Codec.BOOL.optionalFieldOf("jade_housing", false).forGetter(ArcaneFocusComponent::jadeHousing),
            Codec.INT.optionalFieldOf("durability", BASE_MAX_DURABILITY).forGetter(ArcaneFocusComponent::durability)
    ).apply(instance, ArcaneFocusComponent::new));

    public ArcaneFocusComponent {
        rune0 = clean(rune0);
        rune1 = clean(rune1);
        rune2 = clean(rune2);
        gem0 = clean(gem0);
        gem1 = clean(gem1);
        gem2 = clean(gem2);
        int maxDur = jadeHousing ? JADE_MAX_DURABILITY : BASE_MAX_DURABILITY;
        durability = Math.max(0, Math.min(maxDur, durability));
    }

    private static String clean(String s) {
        if (s == null) {
            return "";
        }
        String trimmed = s.trim();
        return trimmed.length() > 32 ? trimmed.substring(0, 32) : trimmed;
    }

    public static ArcaneFocusComponent read(ItemStack stack) {
        ArcaneFocusComponent val = stack.get(FantasyEarth.ARCANE_FOCUS_COMPONENT.get());
        return val == null ? DEFAULT : val;
    }

    public int maxDurability() {
        return jadeHousing ? JADE_MAX_DURABILITY : BASE_MAX_DURABILITY;
    }

    public List<String> runes() {
        return List.of(rune0, rune1, rune2);
    }

    public List<String> gems() {
        return List.of(gem0, gem1, gem2);
    }

    public boolean hasAnyRune() {
        return !rune0.isEmpty() || !rune1.isEmpty() || !rune2.isEmpty();
    }

    /** Highest tier of the requested rune kind ('power', 'guard', 'flow', 'bind', 'jade') in the 3 rune slots. */
    public int maxRuneTier(String kind) {
        String target = kind.toLowerCase(Locale.ROOT);
        int best = 0;
        for (String slot : runes()) {
            if (slot.isEmpty()) {
                continue;
            }
            String[] parts = slot.split(":");
            String slotKind = parts[0].toLowerCase(Locale.ROOT);
            int tier = parts.length > 1 ? parseTier(parts[1]) : 1;
            if (slotKind.equals(target) || "jade".equals(slotKind)) {
                best = Math.max(best, tier);
            }
        }
        return best;
    }

    private static int parseTier(String s) {
        try {
            return Math.max(1, Math.min(4, Integer.parseInt(s.trim())));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public ArcaneFocusComponent withRuneSlot(int index, String spec) {
        return switch (index) {
            case 0 -> new ArcaneFocusComponent(spec, rune1, rune2, gem0, gem1, gem2, jadeHousing, durability);
            case 1 -> new ArcaneFocusComponent(rune0, spec, rune2, gem0, gem1, gem2, jadeHousing, durability);
            case 2 -> new ArcaneFocusComponent(rune0, rune1, spec, gem0, gem1, gem2, jadeHousing, durability);
            default -> this;
        };
    }

    public ArcaneFocusComponent withGemSlot(int index, String spec) {
        return switch (index) {
            case 0 -> new ArcaneFocusComponent(rune0, rune1, rune2, spec, gem1, gem2, jadeHousing, durability);
            case 1 -> new ArcaneFocusComponent(rune0, rune1, rune2, gem0, spec, gem2, jadeHousing, durability);
            case 2 -> new ArcaneFocusComponent(rune0, rune1, rune2, gem0, gem1, spec, jadeHousing, durability);
            default -> this;
        };
    }

    public ArcaneFocusComponent withJadeHousing(boolean jade) {
        int bonus = (jade && !jadeHousing) ? 100 : 0;
        return new ArcaneFocusComponent(rune0, rune1, rune2, gem0, gem1, gem2, jade, durability + bonus);
    }

    public ArcaneFocusComponent withDurability(int nextDurability) {
        return new ArcaneFocusComponent(rune0, rune1, rune2, gem0, gem1, gem2, jadeHousing, nextDurability);
    }

    /** F-RUNE-04: Gem grade multiplier: crude 0.5, common 1.0, fine 1.5, masterwork 2.0, heirloom 3.0. */
    public static double gradeMultiplier(String grade) {
        if (grade == null) {
            return 1.0D;
        }
        return switch (grade.trim().toLowerCase(Locale.ROOT)) {
            case "crude" -> 0.5D;
            case "fine" -> 1.5D;
            case "masterwork" -> 2.0D;
            case "heirloom" -> 3.0D;
            default -> 1.0D;
        };
    }

    public boolean hasDuplicateGemSpecies(String species) {
        String norm = species.trim().toLowerCase(Locale.ROOT);
        for (String slot : gems()) {
            if (!slot.isEmpty() && slot.split(":")[0].equalsIgnoreCase(norm)) {
                return true;
            }
        }
        return false;
    }

    public double gemBonus(String targetSpecies) {
        double total = 0.0D;
        for (String slot : gems()) {
            if (slot.isEmpty()) {
                continue;
            }
            String[] parts = slot.split(":");
            if (parts[0].equalsIgnoreCase(targetSpecies)) {
                String grade = parts.length > 1 ? parts[1] : "common";
                double base = switch (targetSpecies.toLowerCase(Locale.ROOT)) {
                    case "ruby" -> 1.0D;
                    case "sapphire" -> 40.0D;
                    case "emerald" -> 60.0D;
                    case "amethyst" -> 40.0D;
                    case "topaz" -> 2.0D;
                    case "diamond" -> 0.03D;
                    default -> 0.0D;
                };
                total += base * gradeMultiplier(grade);
            }
        }
        return total;
    }
}
