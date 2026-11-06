package com.xxsx.fantasyearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

/** F-024..F-027, F-029 Rune item tier & gem grade component. */
public record RuneItemComponent(String runeKind, int tier, String gemGrade) {
    public static final RuneItemComponent DEFAULT = new RuneItemComponent("power", 1, "common");

    public static final Codec<RuneItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("rune_kind", "power").forGetter(RuneItemComponent::runeKind),
            Codec.INT.optionalFieldOf("tier", 1).forGetter(RuneItemComponent::tier),
            Codec.STRING.optionalFieldOf("gem_grade", "common").forGetter(RuneItemComponent::gemGrade)
    ).apply(instance, RuneItemComponent::new));

    public RuneItemComponent {
        runeKind = runeKind == null || runeKind.isBlank() ? "power" : runeKind;
        int maxTier = "jade".equals(runeKind) ? 4 : 3;
        tier = Math.max(1, Math.min(maxTier, tier));
        gemGrade = gemGrade == null || gemGrade.isBlank() ? "common" : gemGrade;
    }

    public static RuneItemComponent read(ItemStack stack, String defaultKind) {
        RuneItemComponent val = stack.get(FantasyEarth.RUNE_ITEM_COMPONENT.get());
        return val == null ? new RuneItemComponent(defaultKind, 1, "common") : val;
    }

    public RuneItemComponent withTier(int nextTier) {
        return new RuneItemComponent(runeKind, nextTier, gemGrade);
    }

    public RuneItemComponent withGrade(String nextGrade) {
        return new RuneItemComponent(runeKind, tier, nextGrade);
    }
}
