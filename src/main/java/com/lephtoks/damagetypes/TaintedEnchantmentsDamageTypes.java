package com.lephtoks.damagetypes;

import com.lephtoks.TaintboundMod;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface TaintedEnchantmentsDamageTypes {
    RegistryKey<DamageType> HEAT_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(TaintboundMod.MOD_ID, "heat_damage"));
}
