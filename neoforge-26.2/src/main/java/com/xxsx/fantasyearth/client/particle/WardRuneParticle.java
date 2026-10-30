package com.xxsx.fantasyearth.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** A single expanding sigil used by wards and ritual feedback. */
public final class WardRuneParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float baseSize;
    private final float spin;

    private WardRuneParticle(ClientLevel level, double x, double y, double z,
                             double vx, double vy, double vz, SpriteSet sprites,
                             float red, float green, float blue, int lifetime) {
        super(level, x, y, z, vx, vy, vz, sprites.first());
        this.sprites = sprites;
        this.friction = 0.97F;
        this.gravity = -0.002F;
        this.hasPhysics = false;
        this.baseSize = this.quadSize * 1.6F + this.random.nextFloat() * 0.05F;
        this.quadSize = this.baseSize;
        this.lifetime = lifetime;
        this.spin = (this.random.nextFloat() - 0.5F) * 0.16F;
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
        this.setColor(red, green, blue);
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
        float grow = 1.0F - (1.0F - life) * (1.0F - life);
        return this.baseSize * (0.45F + grow * 1.35F);
    }

    @Override
    public void tick() {
        this.oRoll = this.roll;
        this.roll += this.spin;
        super.tick();
        float life = (float) this.age / this.lifetime;
        this.alpha = Mth.clamp((1.0F - life) * 1.25F, 0.0F, 0.85F);
        this.setSpriteFromAge(this.sprites);
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final float red;
        private final float green;
        private final float blue;
        private final int lifetime;

        public Provider(SpriteSet sprites) {
            this(sprites, 0.80F, 0.62F, 1.0F, 30);
        }

        public Provider(SpriteSet sprites, float red, float green, float blue, int lifetime) {
            this.sprites = sprites;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.lifetime = lifetime;
        }

        @Override
        public WardRuneParticle createParticle(SimpleParticleType type, ClientLevel level,
                                               double x, double y, double z,
                                               double vx, double vy, double vz, RandomSource random) {
            return new WardRuneParticle(level, x, y, z,
                    vx * 0.25D,
                    0.006D + random.nextDouble() * 0.006D,
                    vz * 0.25D,
                    this.sprites, this.red, this.green, this.blue,
                    this.lifetime + random.nextInt(8));
        }
    }
}
