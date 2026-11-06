package com.xxsx.fantasyearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

/** F-032 Inscribed Ritual Core component (rune_set 0..64, tier 1..3). */
public record RitualCoreComponent(String runeSet, int tier) {
    public static final RitualCoreComponent DEFAULT = new RitualCoreComponent("power+flow", 1);

    public static final Codec<RitualCoreComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.string(0, 64).optionalFieldOf("rune_set", "power+flow").forGetter(RitualCoreComponent::runeSet),
            Codec.INT.optionalFieldOf("tier", 1).forGetter(RitualCoreComponent::tier)
    ).apply(instance, RitualCoreComponent::new));

    public RitualCoreComponent {
        runeSet = runeSet == null ? "" : (runeSet.length() > 64 ? runeSet.substring(0, 64) : runeSet);
        tier = Math.max(1, Math.min(3, tier));
    }

    public static RitualCoreComponent read(ItemStack stack) {
        RitualCoreComponent val = stack.get(FantasyEarth.RITUAL_CORE_COMPONENT.get());
        return val == null ? DEFAULT : val;
    }

    public RitualCoreComponent withTier(int nextTier) {
        return new RitualCoreComponent(runeSet, nextTier);
    }
}
