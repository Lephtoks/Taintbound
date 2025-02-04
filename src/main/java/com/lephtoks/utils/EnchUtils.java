package com.lephtoks.utils;

import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class EnchUtils {
    public static <T> boolean ifHas(ItemStack itemStack, ComponentType<T> effect, BiConsumer<T, Integer> then) {
        var component = EnchantmentHelper.getEnchantments(itemStack);
        for (RegistryEntry<Enchantment> entry : component.getEnchantments()) {
            var instance = entry.value().effects().get(effect);
            if (instance!=null) {
                then.accept(instance, component.getLevel(entry));
                return true;
            }
        }
        return false;
    }
    public static <T> boolean ifHas(ItemStack itemStack, ComponentType<T> effect, BiPredicate<T, Integer> then) {
        var component = EnchantmentHelper.getEnchantments(itemStack);
        for (RegistryEntry<Enchantment> entry : component.getEnchantments()) {
            var instance = entry.value().effects().get(effect);
            if (instance!=null) {
                return then.test(instance, component.getLevel(entry));
            }
        }
        return false;
    }
}
