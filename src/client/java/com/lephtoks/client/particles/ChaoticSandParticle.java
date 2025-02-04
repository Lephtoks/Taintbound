package com.lephtoks.client.particles;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ChaoticSandParticle extends SpriteBillboardParticle {
    double xIntensity;
    double zIntensity;
    float swingRotation;
    float rotationSpeed;

    protected ChaoticSandParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f);
        this.velocityX = g;
        this.velocityY = -this.random.nextFloat() * 0.025 - 0.02;
        this.velocityZ = i;
        this.rotationSpeed =  (this.random.nextFloat()-0.5f)*10;
        this.xIntensity = (this.random.nextFloat()-0.5)*0.05;
        this.zIntensity = (this.random.nextFloat()-0.5)*0.05;
        this.swingRotation = this.random.nextBetween(0, 360);
        this.x = d;
        this.y = e;
        this.z = f;
        this.scale = 0.2f;
        this.maxAge = (int)(Math.random() * 25.0) + 100;


    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        float f = (float)this.age / (float)this.maxAge;
        f *= f;
        f *= f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k += (int)(f * 15.0F * 16.0F);
        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            Vec3d shift = new Vec3d(Math.sin(this.age * 10) * xIntensity, 0, Math.sin(this.age * 10) * zIntensity).rotateY(this.swingRotation);
            this.velocityX += shift.x;
            this.velocityZ += shift.z;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
        }
        this.swingRotation += this.rotationSpeed;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ChaoticSandParticle portalParticle = new ChaoticSandParticle(clientWorld, d, e, f, g, h, i);
            portalParticle.setSprite(this.spriteProvider);
            return portalParticle;
        }
    }
}