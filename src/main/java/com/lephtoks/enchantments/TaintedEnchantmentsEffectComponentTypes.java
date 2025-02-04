package com.lephtoks.enchantments;

import com.lephtoks.TaintboundMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class TaintedEnchantmentsEffectComponentTypes {
    public static final ComponentType<List<EnchantmentEffectEntry<EnchantmentEntityEffect>>> BLOCK_BREAK = Registry.register(
            Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
            Identifier.of(TaintboundMod.MOD_ID, "break_block"),
            ComponentType.<List<EnchantmentEffectEntry<EnchantmentEntityEffect>>>builder().codec(EnchantmentEffectEntry.createCodec(EnchantmentEntityEffect.CODEC, LootContextTypes.HIT_BLOCK).listOf()).build()
    );
    public static final ComponentType<GoldRatioSwing> GOLD_RATIO_SWING = Registry.register(
            Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
            Identifier.of(TaintboundMod.MOD_ID, "gold_ratio_swing"),
            ComponentType.<GoldRatioSwing>builder().codec(GoldRatioSwing.CODEC).build()
    );
    public static final ComponentType<RandomDamageEnchantmentEffect> RANDOM_DAMAGE = Registry.register(
            Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
            Identifier.of(TaintboundMod.MOD_ID, "random_damage"),
            ComponentType.<RandomDamageEnchantmentEffect>builder().codec(RandomDamageEnchantmentEffect.CODEC).build());


    public static final MapCodec<AddHeatEnchantmentEffect> ADD_HEAT = Registry.register(
            Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
            Identifier.of(TaintboundMod.MOD_ID, "add_heat"),
            AddHeatEnchantmentEffect.CODEC);

    public static final MapCodec<AddNegatedEffectsEnchantmentEffect> ADD_NEGATED_EFFECTS = Registry.register(
            Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
            Identifier.of(TaintboundMod.MOD_ID, "add_negated_effects"),
            AddNegatedEffectsEnchantmentEffect.CODEC);

    public static final MapCodec<BreakAdjacentEnchantmentEffect> BREAK_ADJACENT = Registry.register(
            Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
            Identifier.of(TaintboundMod.MOD_ID, "break_adjacent"),
            BreakAdjacentEnchantmentEffect.CODEC);

    public static void initialize() {
    }
}
