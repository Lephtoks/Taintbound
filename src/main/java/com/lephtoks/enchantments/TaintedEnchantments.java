package com.lephtoks.enchantments;

import com.lephtoks.TaintboundMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class TaintedEnchantments {

    public static final RegistryKey<Enchantment> EFFICIENCY = of("tainted_efficiency");
    public static final RegistryKey<Enchantment> INFINITY = of("tainted_infinity");
    public static final RegistryKey<Enchantment> SHARPNESS = of("tainted_sharpness");
    public static final RegistryKey<Enchantment> SWEEPING_EDGE = of("tainted_sweeping_edge");
    public static final RegistryKey<Enchantment> DESTABILISATION = of("destabilisation");
    private static RegistryKey<Enchantment> of(String name) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(TaintboundMod.MOD_ID, name));
    }

    public static void initialize() {
    }
}
