package com.lephtoks.challenges;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

import static com.lephtoks.registries.TaintedRegistries.CHALLENGE_SERIALIZER;

public interface ChallengeType {
    public static final Codec<ChallengeType> CODEC = CHALLENGE_SERIALIZER.getCodec().dispatch(ChallengeType::getSerializer, ChallengeSerializer::getCodec);
    Identifier getChallengeTooltipRenderIdentifier();

    ChallengeSerializer getSerializer();
    };
