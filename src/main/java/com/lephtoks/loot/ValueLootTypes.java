package com.lephtoks.loot;

import com.lephtoks.TaintboundMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ValueLootTypes {

    public static final LootNumberProviderType HEAT = register("heat", HeatValueLootType.CODEC);
    private static LootNumberProviderType register(String id, MapCodec<? extends LootNumberProvider> codec) {
        return Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, Identifier.of(TaintboundMod.MOD_ID, id), new LootNumberProviderType(codec));
    }

    public static void init() {}
}
