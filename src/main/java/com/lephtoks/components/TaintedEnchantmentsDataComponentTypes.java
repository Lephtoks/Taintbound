package com.lephtoks.components;

import com.lephtoks.TaintboundMod;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TaintedEnchantmentsDataComponentTypes {
    public static final ComponentType<BrokenEnchantmentAbilityComponent> BROKEN_ENCHANTMENT_ABILITY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TaintboundMod.MOD_ID, "broken_enchantment_ability"),
            ComponentType.<BrokenEnchantmentAbilityComponent>builder().codec(BrokenEnchantmentAbilityComponent.CODEC).packetCodec(BrokenEnchantmentAbilityComponent.PACKET_CODEC).build()
    );

    public static final ComponentType<ChallengeComponent> CHALLENGE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(TaintboundMod.MOD_ID, "challenge"),
            ComponentType.<ChallengeComponent>builder().codec(ChallengeComponent.CODEC).packetCodec(ChallengeComponent.PACKET_CODEC).build()
    );

    public static void initialize() {}
}
