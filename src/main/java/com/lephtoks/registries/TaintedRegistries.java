package com.lephtoks.registries;

import com.lephtoks.challenges.ChallengeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.lephtoks.challenges.ChallengeTypes.CHALLENGE_SERIALIZER_KEY;
import static com.lephtoks.challenges.serializers.ChallengeSerializers.ITEM_CHALLENGE_SERIALIZER;

public class TaintedRegistries {
    public static final Registry<ChallengeSerializer> CHALLENGE_SERIALIZER;
    public static void init() {}
    static {
        CHALLENGE_SERIALIZER = Registries.create(CHALLENGE_SERIALIZER_KEY,  (registry) -> ITEM_CHALLENGE_SERIALIZER);
    }
}
