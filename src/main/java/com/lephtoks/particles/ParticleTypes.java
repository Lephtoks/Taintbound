package com.lephtoks.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static com.lephtoks.TaintboundMod.MOD_ID;

public class ParticleTypes {
    public static final SimpleParticleType CORRUPTION_PARTICLE = register("corruption", false);
    public static final SimpleParticleType CHAOTIC_SAND = register("chaotic_sand", false);


    private static <T extends ParticleEffect> ParticleType<T> register(String name, boolean alwaysShow, final Function<ParticleType<T>, MapCodec<T>> codecGetter, final Function<ParticleType<T>, PacketCodec<? super RegistryByteBuf, T>> packetCodecGetter) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, name), new ParticleType<T>(alwaysShow) {
            public MapCodec<T> getCodec() {
                return codecGetter.apply(this);
            }

            public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
                return packetCodecGetter.apply(this);
            }
        });
    }
    private static SimpleParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, name), new SimpleParticleType(alwaysShow) {});
    }

    public static void spawnParticles() {

    }
}
