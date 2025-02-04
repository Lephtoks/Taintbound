package com.lephtoks.enchantments;

import com.lephtoks.TaintboundMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface TaintedEnchantmentsTag {
    TagKey<Enchantment> TAINTED_ENCHANTMENTS_SET = of("tainted_enchantments");
    TagKey<Enchantment> CAN_GET_CHALLENGE = of("can_get_challenge");

    private static TagKey<Enchantment> of(String id) {
        return TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(TaintboundMod.MOD_ID, id));
    }
}