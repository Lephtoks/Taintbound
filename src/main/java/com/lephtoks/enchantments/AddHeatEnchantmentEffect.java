package com.lephtoks.enchantments;

import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record AddHeatEnchantmentEffect(EnchantmentLevelBasedValue value) implements EnchantmentEntityEffect {
    public static final MapCodec<AddHeatEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("value").forGetter((addHeatEnchantmentEffect) -> {
            return addHeatEnchantmentEffect.value;
        })).apply(instance, AddHeatEnchantmentEffect::new);
    });

    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        PlayerDataAccessor data = (PlayerDataAccessor) user;
        data.taintedEnchantments$setHeat(this.value().getValue(level) + data.taintedEnchantments$getHeat());
    }

    public MapCodec<AddHeatEnchantmentEffect> getCodec() {
        return CODEC;
    }

    public EnchantmentLevelBasedValue value() {
        return this.value;
    }
}