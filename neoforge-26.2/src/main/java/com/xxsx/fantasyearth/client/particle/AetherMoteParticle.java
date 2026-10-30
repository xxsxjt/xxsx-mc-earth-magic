package com.xxsx.fantasyearth.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** Slow drifting motes that carry the arcane field. */
public final class AetherMoteParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float twinklePhase;

    private AetherMoteParticle(ClientLevel level, double x, double y, double z,
                               double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz, sprites.first());
        this.sprites = sprites;
        this.friction = 0.93F;
        this.gravity = -0.004F;
        this.hasPhysics = false;
        this.quadSize *= 0.10F + this.random.nextFloat() * 0.06F;
        this.setLifetime(28 + this.random.nextInt(26));
        this.twinklePhase = this.random.nextFloat() * Mth.TWO_PI;
        float warmth = this.random.nextFloat();
        this.setColor(0.62F + warmth * 0.30F, 0.90F + warmth * 0.10F, 1.0F);
        this.setSpriteFromAge(sprites);
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(partialTick), 1.0F);
    }

    @Override
    public float getQuadSize(float partialTick) {
        float life = (this.age + partialTick) / this.lifetime;
        float grow = Mth.sin(Math.min(1.0F, life * 1.15F) * Mth.PI);
        return this.quadSize * (0.55F + grow * 0.65F);
    }

    @Override
    public void tick() {
        super.tick();
        float life = (float) this.age / this.lifetime;
        this.alpha = Mth.clamp((1.0F - life) * 1.4F, 0.0F, 1.0F);
        float twinkle = 0.82F + Mth.sin(this.age * 0.55F + this.twinklePhase) * 0.18F;
        this.setColor(0.62F * twinkle, 0.94F * twinkle, twinkle);
        this.setSpriteFromAge(this.sprites);
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public AetherMoteParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                 double x, double y, double z,
                                                 double vx, double vy, double vz, RandomSource random) {
            return new AetherMoteParticle(level, x, y, z,
                    vx + (random.nextDouble() - 0.5D) * 0.012D,
                    vy + 0.004D + random.nextDouble() * 0.008D,
                    vz + (random.nextDouble() - 0.5D) * 0.012D,
                    this.sprites);
        }
    }
}
