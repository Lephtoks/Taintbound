package com.lephtoks.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class GoldRatioCriterion extends AbstractCriterion<GoldRatioCriterion.Conditions> {
    public GoldRatioCriterion() {
    }

    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, int combo) {
        this.trigger(player, (conditions) -> conditions.matches(combo));
    }

    public record Conditions(Optional<LootContextPredicate> player, int value) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                Codec.INT.optionalFieldOf("combo", 10).forGetter(Conditions::value)
        ).apply(instance, Conditions::new));

        public static AdvancementCriterion<Conditions> any() {
            return TaintboundAdvancements.GOLD_RATIO.create(new Conditions(Optional.empty(), 10));
        }

        public static AdvancementCriterion<Conditions> heat(int value) {
            return TaintboundAdvancements.GOLD_RATIO.create(new Conditions(Optional.empty(), value));
        }

        public boolean matches(int combo) {
            return this.value <= combo;
        }
    }
}
