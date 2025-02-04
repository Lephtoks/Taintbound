package com.lephtoks.client.emi.recipes;

import com.lephtoks.client.emi.EMIIntegration;
import com.lephtoks.enchantments.TaintedEnchantments;
import com.lephtoks.recipes.destabilisation.DestabilisationRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

public class DestabilisationEmiCraft extends TaintboundEMIRecipe<DestabilisationRecipe> {
    private final EmiStack enchantment;
    public DestabilisationEmiCraft(RecipeEntry<DestabilisationRecipe> recipe) {
        super(recipe);
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            EnchantmentHelper.apply(stack, (builder -> {
                Optional<RegistryEntry.Reference<Enchantment>> ench = world.getRegistryManager().createRegistryLookup().getOptionalEntry(RegistryKeys.ENCHANTMENT, TaintedEnchantments.DESTABILISATION);
                ench.ifPresent(enchantmentReference -> builder.set(enchantmentReference, 1));
            }));
        }
        enchantment = EmiStack.of(stack);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIIntegration.DESTABILISATION;
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 30;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.addSlot(getInputs().getFirst(), 19+5, 7-1).recipeContext(this);
        widgetHolder.addSlot(enchantment, 1+5, 7-1).recipeContext(this);
        widgetHolder.addTexture(EmiTexture.EMPTY_ARROW, 19+30+5, 7-1);
        widgetHolder.addSlot(getOutputs().getFirst(), 19+66+5-1, 7-4-1).large(true).recipeContext(this);
    }
}
