package com.lephtoks.challenges;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.lephtoks.TaintboundMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

public class ChallengeTypes {
    public static final RegistryKey<Registry<ChallengeType>> CHALLENGE_TYPE_KEY = RegistryKey.ofRegistry(Identifier.of(TaintboundMod.MOD_ID, "challenge_type"));
    public static final RegistryKey<Registry<ChallengeSerializer>> CHALLENGE_SERIALIZER_KEY = RegistryKey.ofRegistry(Identifier.of(TaintboundMod.MOD_ID, "challenge_serializer"));

    private static final BiMap<Identifier, ChallengeType> MAP = HashBiMap.create();

    public final static PacketCodec<RegistryByteBuf, ChallengeType> PACKET_CODEC;
    public static ChallengeType register(String name, Supplier<ChallengeType> challengeTypeSupplier) {
        return register(Identifier.of(TaintboundMod.MOD_ID, name), challengeTypeSupplier);
    }
    public static ChallengeType register(Identifier identifier, Supplier<ChallengeType> challengeTypeSupplier) {
        ChallengeType challengeType = challengeTypeSupplier.get();
        if (MAP.put(identifier, challengeType) != null) {
            throw new IllegalStateException("Challenge parameter set " + identifier + " is already registered");
        } else {
            return challengeType;
        }
    }

    static {
        BiMap<? extends ChallengeType, Identifier> var10002 = MAP.inverse();
        Objects.requireNonNull(var10002);

        PACKET_CODEC = PacketCodecs.registryValue(CHALLENGE_SERIALIZER_KEY).dispatch(ChallengeType::getSerializer, ChallengeSerializer::getPacketCodec);

    }
}
