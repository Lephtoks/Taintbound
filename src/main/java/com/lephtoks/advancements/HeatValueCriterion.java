package com.lephtoks.advancements;

import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class HeatValueCriterion extends AbstractCriterion<HeatValueCriterion.Conditions> {
    public HeatValueCriterion() {
    }

    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, (conditions) -> conditions.matches(((PlayerDataAccessor) player).taintedEnchantments$getHeat()));
    }

    public record Conditions(Optional<LootContextPredicate> player, double value) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                Codec.DOUBLE.optionalFieldOf("value", 10d).forGetter(Conditions::value)
        ).apply(instance, Conditions::new));

        public static AdvancementCriterion<Conditions> any() {
            return TaintboundAdvancements.HEAT_VALUE.create(new Conditions(Optional.empty(), 10d));
        }

        public static AdvancementCriterion<Conditions> heat(double value) {
            return TaintboundAdvancements.HEAT_VALUE.create(new Conditions(Optional.empty(), value));
        }

        public boolean matches(double heat) {
            return this.value <= heat;
        }
    }
}
