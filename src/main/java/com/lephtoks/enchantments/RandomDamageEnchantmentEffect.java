package com.lephtoks.enchantments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;

import java.util.List;

public record RandomDamageEnchantmentEffect(List<EnchantmentLevelBasedValue> weights, List<EnchantmentLevelBasedValue> modifiers) {
    public static final Codec<RandomDamageEnchantmentEffect> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.list(EnchantmentLevelBasedValue.CODEC).fieldOf("weights").forGetter(RandomDamageEnchantmentEffect::weights),
                Codec.list(EnchantmentLevelBasedValue.CODEC).fieldOf("modifiers").forGetter(RandomDamageEnchantmentEffect::modifiers)
        ).apply(instance, RandomDamageEnchantmentEffect::new);
    });
}