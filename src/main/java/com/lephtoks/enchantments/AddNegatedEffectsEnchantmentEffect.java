package com.lephtoks.enchantments;

import com.lephtoks.mixinaccessors.PersistenProjectileEntityAccessor;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record AddNegatedEffectsEnchantmentEffect(EnchantmentLevelBasedValue duration, EnchantmentLevelBasedValue amplifier) implements EnchantmentEntityEffect {
    public static final MapCodec<AddNegatedEffectsEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                EnchantmentLevelBasedValue.CODEC.fieldOf("duration").forGetter(AddNegatedEffectsEnchantmentEffect::duration),
                EnchantmentLevelBasedValue.CODEC.fieldOf("amplifier").forGetter(AddNegatedEffectsEnchantmentEffect::amplifier)
        ).apply(instance, AddNegatedEffectsEnchantmentEffect::new);
    });
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        if (user instanceof PersistentProjectileEntity arrow) {
            ((PersistenProjectileEntityAccessor) arrow).setCorruption(true);
        }
    }

    public MapCodec<AddNegatedEffectsEnchantmentEffect> getCodec() {
        return CODEC;
    }

    public EnchantmentLevelBasedValue duration() {
        return this.duration;
    }

    @Override
    public EnchantmentLevelBasedValue amplifier() {
        return amplifier;
    }
}