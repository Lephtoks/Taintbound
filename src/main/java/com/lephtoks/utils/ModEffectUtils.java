package com.lephtoks.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModEffectUtils {
    public static final BiMap<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> REVERSED_EFFECTS = HashBiMap.create();
    private static void add(RegistryEntry<StatusEffect> a, RegistryEntry<StatusEffect> b) {REVERSED_EFFECTS.put(a, b); }
    static {
        add(StatusEffects.REGENERATION, StatusEffects.POISON);
        add(StatusEffects.SATURATION, StatusEffects.HUNGER);
        add(StatusEffects.NIGHT_VISION, StatusEffects.BLINDNESS);
        add(StatusEffects.HASTE, StatusEffects.MINING_FATIGUE);
        add(StatusEffects.DOLPHINS_GRACE, StatusEffects.NAUSEA);
        add(StatusEffects.HERO_OF_THE_VILLAGE, StatusEffects.BAD_OMEN);
        add(StatusEffects.INVISIBILITY, StatusEffects.GLOWING);
        add(StatusEffects.STRENGTH, StatusEffects.WEAKNESS);
        add(StatusEffects.SPEED, StatusEffects.SLOWNESS);
        add(StatusEffects.WATER_BREATHING, StatusEffects.FIRE_RESISTANCE);
        add(StatusEffects.JUMP_BOOST, StatusEffects.LEVITATION);
        add(StatusEffects.WITHER, StatusEffects.INSTANT_HEALTH);
        add(StatusEffects.RESISTANCE, StatusEffects.INSTANT_DAMAGE);
    }
    @Nullable
    public static RegistryEntry<StatusEffect> getReversed(@NotNull RegistryEntry<StatusEffect> effect) {
        if (REVERSED_EFFECTS.containsKey(effect)) {
            return REVERSED_EFFECTS.get(effect);
        }
        else {
            var m = REVERSED_EFFECTS.inverse();
            return m.getOrDefault(effect, null);
        }
    }
}
