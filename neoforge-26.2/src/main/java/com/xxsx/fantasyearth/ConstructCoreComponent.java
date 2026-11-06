package com.xxsx.fantasyearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

/** F-037 Construct Core component (charge_AEU 0..240, bound_golem UUID). */
public record ConstructCoreComponent(double chargeAeu, String boundGolemUuid) {
    public static final double MAX_CHARGE_AEU = 240.0D;
    public static final ConstructCoreComponent FULL = new ConstructCoreComponent(MAX_CHARGE_AEU, "");
    public static final ConstructCoreComponent EMPTY = new ConstructCoreComponent(0.0D, "");

    public static final Codec<ConstructCoreComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("charge_AEU", MAX_CHARGE_AEU).forGetter(ConstructCoreComponent::chargeAeu),
            Codec.STRING.optionalFieldOf("bound_golem", "").forGetter(ConstructCoreComponent::boundGolemUuid)
    ).apply(instance, ConstructCoreComponent::new));

    public ConstructCoreComponent {
        chargeAeu = Double.isFinite(chargeAeu) ? Math.max(0.0D, Math.min(MAX_CHARGE_AEU, chargeAeu)) : 0.0D;
        boundGolemUuid = boundGolemUuid == null ? "" : boundGolemUuid;
    }

    public static ConstructCoreComponent read(ItemStack stack) {
        ConstructCoreComponent val = stack.get(FantasyEarth.CONSTRUCT_CORE_COMPONENT.get());
        return val == null ? FULL : val;
    }

    public Optional<UUID> boundGolem() {
        if (boundGolemUuid.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(boundGolemUuid));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public ConstructCoreComponent withCharge(double nextCharge) {
        return new ConstructCoreComponent(nextCharge, boundGolemUuid);
    }

    public ConstructCoreComponent withBoundGolem(UUID uuid) {
        return new ConstructCoreComponent(chargeAeu, uuid == null ? "" : uuid.toString());
    }
}
