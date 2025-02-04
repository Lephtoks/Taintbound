package com.lephtoks.challenges.serializers;

import com.lephtoks.TaintboundMod;
import com.lephtoks.challenges.ChallengeSerializer;
import com.lephtoks.challenges.EntityKillChallengeType;
import com.lephtoks.challenges.ItemChallengeType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.lephtoks.registries.TaintedRegistries.CHALLENGE_SERIALIZER;

public class ChallengeSerializers {
    public static final ChallengeSerializer ITEM_CHALLENGE_SERIALIZER = register("item", new ItemChallengeType.Serializer());
    public static final ChallengeSerializer ENTITY_KILL_CHALLENGE_SERIALIZER = register("entity_kill", new EntityKillChallengeType.Serializer());;


    static <S extends ChallengeSerializer> S register(String id, S serializer) {
        return (S)(Registry.register(CHALLENGE_SERIALIZER, Identifier.of(TaintboundMod.MOD_ID, id), serializer));
    }
}
