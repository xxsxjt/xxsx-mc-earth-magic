package com.xxsx.fantasyearth;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Custom particle types. Client providers are registered in FantasyEarthClient. */
public final class FantasyEarthParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, FantasyEarth.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> AETHER_MOTE =
            PARTICLE_TYPES.register("aether_mote", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RUNE_SPARK =
            PARTICLE_TYPES.register("rune_spark", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CRYSTAL_SHARD =
            PARTICLE_TYPES.register("crystal_shard", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WARD_RUNE =
            PARTICLE_TYPES.register("ward_rune", () -> new SimpleParticleType(false));

    private FantasyEarthParticles() {
    }
}
