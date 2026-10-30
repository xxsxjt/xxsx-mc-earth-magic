package com.xxsx.fantasyearth.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** Fast sharp sparks thrown while a rune is being carved or discharged. */
public final class RuneSparkParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final float spin;

    private RuneSparkParticle(ClientLevel level, double x, double y, double z,
                              double vx, double vy, double vz, SpriteSet sprites,
                              float red, float green, float blue) {
        super(level, x, y, z, vx, vy, vz, sprites.first());
        this.sprites = sprites;
        this.friction = 0.86F;
        this.gravity = 0.02F;
        this.hasPhysics = false;
        this.quadSize *= 0.075F + this.random.nextFloat() * 0.045F;
        this.lifetime = 9 + this.random.nextInt(9);
        this.spin = (this.random.nextFloat() - 0.5F) * 0.9F;
        this.setColor(red, green, blue);
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
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
    public void tick() {
        this.oRoll = this.roll;
        this.roll += this.spin;
        super.tick();
        float life = (float) this.age / this.lifetime;
        this.alpha = Mth.clamp((1.0F - life) * 1.6F, 0.0F, 1.0F);
        this.setSpriteFromAge(this.sprites);
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final float red;
        private final float green;
        private final float blue;

        public Provider(SpriteSet sprites) {
            this(sprites, 0.80F, 0.97F, 1.0F);
        }

        public Provider(SpriteSet sprites, float red, float green, float blue) {
            this.sprites = sprites;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        @Override
        public RuneSparkParticle createParticle(SimpleParticleType type, ClientLevel level,
                                                double x, double y, double z,
                                                double vx, double vy, double vz, RandomSource random) {
            return new RuneSparkParticle(level, x, y, z,
                    vx + (random.nextDouble() - 0.5D) * 0.09D,
                    vy + (random.nextDouble() - 0.2D) * 0.06D,
                    vz + (random.nextDouble() - 0.5D) * 0.09D,
                    this.sprites, this.red, this.green, this.blue);
        }
    }
}
