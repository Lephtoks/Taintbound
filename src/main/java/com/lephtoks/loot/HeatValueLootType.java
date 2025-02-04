package com.lephtoks.loot;

import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;

public record HeatValueLootType(float value) implements LootNumberProvider {
    public static final MapCodec<HeatValueLootType> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.FLOAT.optionalFieldOf("duration", 0f).forGetter(HeatValueLootType::value)).apply(instance, HeatValueLootType::new);
    });

    public LootNumberProviderType getType() {
        return ValueLootTypes.HEAT;
    }

    public float nextFloat(net.minecraft.loot.context.LootContext context) {
        if (context.get(LootContextParameters.THIS_ENTITY) instanceof PlayerEntity player) {
            double heat = ((PlayerDataAccessor) player).getHeat();
            return heat > 10 ? (float) Math.min((heat) * 0.005f + Math.sqrt(heat) * 0.0025f, 1) : 0;
        }
        else {
            return 0;
        }
    }

    public float value() {
        return this.value;
    }
}

