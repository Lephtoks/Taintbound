package com.lephtoks.client.emi.recipes;

import com.lephtoks.client.emi.EMIIntegration;
import com.lephtoks.recipes.taintedtable.TaintedTableRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Taint extends TaintboundEMIRecipe<TaintedTableRecipe> {

    private final EmiStack inputEnchantments;
    private final EmiStack outputEnchantments;
    public Taint(RecipeEntry<TaintedTableRecipe> recipe) {
        super(recipe);
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.set(stack, recipe.value().getInputEnchantments());
        inputEnchantments = EmiStack.of(stack);
        stack = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.set(stack, recipe.value().getResultEnchant());
        outputEnchantments = EmiStack.of(stack);

    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(this.outputEnchantments);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIIntegration.TAINT;
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return 100;
    }
    private int lastTick = 0;
    @Override
    public void addWidgets(WidgetHolder widgets) {
        var inputs = getInputs();
        var outputs = getOutputs();

        widgets.addSlot(outputs.getFirst(), getDisplayWidth()/2 - 12, getDisplayHeight()/2 - 12).large(true).recipeContext(this);
        widgets.addSlot(inputEnchantments, 0, 0).recipeContext(this);

        float currentRotation = -3.14f*0.5f;
        float adder = 3.14f*2 / inputs.size();
        for (EmiIngredient ingredient : inputs) {
            int radius = 40;
            widgets.addSlot(ingredient, ((int) (radius * Math.cos(currentRotation) + getDisplayWidth()*0.5-8)), ((int) (radius * Math.sin(currentRotation) + getDisplayHeight()*0.5 - 8)));
            currentRotation += adder;
        }

    }

    @Override
    public boolean supportsRecipeTree() {
        return true;
    }

    @Override
    public boolean hideCraftable() {
        return false;
    }

    @Override
    public @Nullable RecipeEntry<?> getBackingRecipe() {
        return null;
    }
}