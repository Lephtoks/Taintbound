package com.lephtoks.items;


import com.lephtoks.TaintboundMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface TaintedEnchantmentsItemTags {
    TagKey<Item> HEAT_REDUCE = of("heat_reduce");

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(TaintboundMod.MOD_ID, id));
    }
}