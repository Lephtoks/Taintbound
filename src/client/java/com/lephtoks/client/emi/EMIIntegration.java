package com.lephtoks.client.emi;

import com.lephtoks.TaintboundMod;
import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.client.emi.recipes.DestabilisationEmiCraft;
import com.lephtoks.client.emi.recipes.HollowCoreEmiCraft;
import com.lephtoks.client.emi.recipes.Taint;
import com.lephtoks.client.emi.recipes.TaintboundEMIRecipe;
import com.lephtoks.enchantments.TaintedEnchantmentsTag;
import com.lephtoks.items.TaintedItems;
import com.lephtoks.recipes.TaintboundRecipes;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class EMIIntegration implements EmiPlugin {

    public static final EmiRecipeCategory TAINT = new EmiRecipeCategory(Identifier.of(TaintboundMod.MOD_ID, "taint"), EmiStack.of(TaintedBlocks.TAINTED_TABLE));
    public static final EmiRecipeCategory HOLLOW_CORE = new EmiRecipeCategory(Identifier.of(TaintboundMod.MOD_ID, "hollow_core"), EmiStack.of(TaintedBlocks.HOLLOW_CORE));
    public static final EmiRecipeCategory DESTABILISATION = new EmiRecipeCategory(Identifier.of(TaintboundMod.MOD_ID, "destabilisation"), EmiStack.of(TaintedBlocks.TAINTED_TABLE));
    public static final EmiRecipeCategory CHALLENGE = new EmiRecipeCategory(Identifier.of(TaintboundMod.MOD_ID, "challenge"), EmiStack.of(TaintedItems.DARK_GRIMOIRE));
    @Override
    public void initialize(EmiInitRegistry registry) {
//        registry.disableStacks((s) -> true);
//        registry.disableStacks((stack) -> stack.getItemStack().getItem() == Items.ENCHANTED_BOOK && filterEnchantments(stack.getItemStack()));
    }
    private boolean filterEnchantments(ItemStack itemStack) {
        ItemEnchantmentsComponent itemEnchantmentsComponent = itemStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

        for(Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
            RegistryEntry<Enchantment> registryEntry = entry.getKey();
            if (registryEntry.isIn(TaintedEnchantmentsTag.TAINTED_ENCHANTMENTS_SET)) {
                int level = itemEnchantmentsComponent.getLevel(registryEntry);
                return registryEntry.value().getMaxLevel() > level || level > 1;
            }
        }
        return false;

    }


    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(TAINT);
        registry.addWorkstation(TAINT, EmiStack.of(TaintedBlocks.TAINTED_TABLE));
        registerAllCrafts(registry, TaintboundRecipes.TET_RECIPE, Taint::new);

        registry.addCategory(HOLLOW_CORE);
        registry.addWorkstation(HOLLOW_CORE, EmiStack.of(TaintedBlocks.HOLLOW_CORE));
        registry.addWorkstation(HOLLOW_CORE, EmiStack.of(TaintedBlocks.HOLLOW_CORE_WITH_DAYLIGHT_SENSOR));
        registerAllCrafts(registry, TaintboundRecipes.HC_RECIPE, HollowCoreEmiCraft::new);

        registry.addCategory(DESTABILISATION);
        registry.addWorkstation(DESTABILISATION, EmiStack.of(TaintedBlocks.TAINTED_TABLE));
        registerAllCrafts(registry, TaintboundRecipes.DESTABILISATION_RECIPE, DestabilisationEmiCraft::new);
    }
    public <I extends RecipeInput, M extends Recipe<I>> void registerAllCrafts(
            EmiRegistry registry,
            RecipeType<M> recipeType,
            Function<RecipeEntry<M>, TaintboundEMIRecipe<?>> factory
    ) {
        registry.getRecipeManager()
                .listAllOfType(recipeType)
                .forEach(entry -> registry.addRecipe(factory.apply(entry)));
    }
}