package com.xxsx.fantasyearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

/** F-048 Spell Scroll component (graph_id 0..64, charges 1). */
public record SpellScrollComponent(String graphId, int charges) {
    public static final SpellScrollComponent EMPTY = new SpellScrollComponent("", 1);

    public static final Codec<SpellScrollComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.string(0, 64).optionalFieldOf("graph_id", "").forGetter(SpellScrollComponent::graphId),
            Codec.INT.optionalFieldOf("charges", 1).forGetter(SpellScrollComponent::charges)
    ).apply(instance, SpellScrollComponent::new));

    public SpellScrollComponent {
        graphId = graphId == null ? "" : (graphId.length() > 64 ? graphId.substring(0, 64) : graphId);
        charges = 1;
    }

    public static SpellScrollComponent read(ItemStack stack) {
        SpellScrollComponent val = stack.get(FantasyEarth.SPELL_SCROLL_COMPONENT.get());
        return val == null ? EMPTY : val;
    }

    public SpellScrollComponent withGraph(String nextGraphId) {
        return new SpellScrollComponent(nextGraphId, 1);
    }
}
