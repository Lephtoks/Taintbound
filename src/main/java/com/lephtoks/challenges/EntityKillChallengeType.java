package com.lephtoks.challenges;

import com.lephtoks.TaintboundMod;
import com.lephtoks.challenges.serializers.ChallengeSerializers;
import com.lephtoks.components.ChallengeComponent;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public record EntityKillChallengeType(EntityType<?> entityType) implements ChallengeTypeE1<LivingEntity> {
    public static final Identifier RENDER = Identifier.of(TaintboundMod.MOD_ID, "entity_kill");

    @Override
    public Identifier getChallengeTooltipRenderIdentifier() { return RENDER; }

    @Override
    public ChallengeSerializer getSerializer() {
        return ChallengeSerializers.ENTITY_KILL_CHALLENGE_SERIALIZER;
    }

    @Override
    public boolean matches(ChallengeComponent challenge, LivingEntity entry) {
        return entry.getType() == entityType;
    }

    public static class Serializer implements ChallengeSerializer {
        final MapCodec<EntityKillChallengeType> codec;
        final PacketCodec<RegistryByteBuf, EntityKillChallengeType> packetCodec;
        public Serializer() {

            this.codec = RecordCodecBuilder.mapCodec((instance) -> {
                Products.P1<RecordCodecBuilder.Mu<EntityKillChallengeType>, EntityType<?>> var10000 = instance.group(
                        Registries.ENTITY_TYPE.getCodec().fieldOf("entity").forGetter(EntityKillChallengeType::entityType)
                );

                return var10000.apply(instance, EntityKillChallengeType::new);
            });

            this.packetCodec = PacketCodec.tuple(

                    PacketCodecs.registryCodec(Registries.ENTITY_TYPE.getCodec()),
                    EntityKillChallengeType::entityType,

            EntityKillChallengeType::new);
        }
        @Override
        public MapCodec<? extends ChallengeType> getCodec() {
            return this.codec;
        }

        @Override
        public PacketCodec<? super RegistryByteBuf, ? extends ChallengeType> getPacketCodec() {
            return this.packetCodec;
        }
    }
}
