package com.xxsx.fantasyearth.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** Shards that tumble off crystal armour and aether clusters. */
public final class CrystalShardParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float spin;

    private CrystalShardParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz, sprites.first());
        this.sprites = sprites;
        this.friction = 0.94F;
        this.gravity = 0.055F;
        this.hasPhysics = true;
        this.quadSize *= 0.065F + this.random.nextFloat() * 0.05F;
        this.lifetime = 18 + this.random.nextInt(16);
        this.spin = (this.random.nextFloat() - 0.5F) * 0.45F;
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
        this.setColor(0.72F, 0.93F, 1.0F);
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(partialTick), 0.6F);
    }

    @Override
    public void tick() {
        this.oRoll = this.roll;
        this.roll += this.spin;
        super.tick();
        float life = (float) this.age / this.lifetime;
        this.alpha = Mth.clamp((1.0F - life) * 1.5F, 0.0F, 1.0F);
        this.setSpriteFromAge(this.sprites);
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public CrystalShardParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                   double x, double y, double z,
                                                   double vx, double vy, double vz, RandomSource random) {
            return new CrystalShardParticle(level, x, y, z,
                    vx + (random.nextDouble() - 0.5D) * 0.11D,
                    vy + random.nextDouble() * 0.10D,
                    vz + (random.nextDouble() - 0.5D) * 0.11D,
                    this.sprites);
        }
    }
}
