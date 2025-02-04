package com.lephtoks.enchantments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;

public record GoldRatioSwing(EnchantmentLevelBasedValue damage, EnchantmentLevelBasedValue acceleration, EnchantmentLevelBasedValue max_combo, EnchantmentLevelBasedValue spread) {
    public static final Codec<GoldRatioSwing> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                EnchantmentLevelBasedValue.CODEC.fieldOf("damage_mul").forGetter(GoldRatioSwing::damage),
                EnchantmentLevelBasedValue.CODEC.fieldOf("acceleration").forGetter(GoldRatioSwing::acceleration),
                EnchantmentLevelBasedValue.CODEC.fieldOf("max_combo").forGetter(GoldRatioSwing::max_combo),
                EnchantmentLevelBasedValue.CODEC.fieldOf("spread").forGetter(GoldRatioSwing::spread)
        ).apply(instance, GoldRatioSwing::new);
    });
}