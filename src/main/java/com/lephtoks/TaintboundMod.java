package com.lephtoks;

import com.lephtoks.advancements.TaintboundAdvancements;
import com.lephtoks.blockentities.TaintedBlockEntityTypes;
import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.components.TaintedEnchantmentsDataComponentTypes;
import com.lephtoks.enchantments.TaintedEnchantments;
import com.lephtoks.enchantments.TaintedEnchantmentsEffectComponentTypes;
import com.lephtoks.items.TaintedItems;
import com.lephtoks.loot.ValueLootTypes;
import com.lephtoks.recipes.TaintboundRecipes;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaintboundMod implements ModInitializer {
	public static final char[] CHARGES = {
			'α', 'β', 'γ', 'δ', 'ε', 'ζ', 'η', 'θ', 'ι', 'κ',
			'λ', 'μ', 'ν', 'ξ', 'ο', 'π', 'ρ', 'σ', 'τ', 'υ',
			'φ', 'χ', 'ψ', 'ω'
	};
	public static final String MOD_ID = "taintbound";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Other initializations
		TaintedEnchantments.initialize();
		TaintedBlocks.initialize();
		TaintedBlockEntityTypes.initialize();
		TaintedEnchantmentsEffectComponentTypes.initialize();
		TaintedEnchantmentsDataComponentTypes.initialize();
		TaintboundRecipes.init();
		ValueLootTypes.init();
		TaintedItems.init();
		TaintboundAdvancements.init();
	}
}