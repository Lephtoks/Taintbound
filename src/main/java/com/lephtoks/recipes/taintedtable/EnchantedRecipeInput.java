package com.lephtoks.recipes.taintedtable;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class EnchantedRecipeInput implements RecipeInput {
    DefaultedList<ItemStack> inventory;
    @Nullable ItemEnchantmentsComponent enchantments;

    public EnchantedRecipeInput(DefaultedList<ItemStack> inventory, ItemEnchantmentsComponent enchantments) {
        this.inventory = inventory;
        this.enchantments = enchantments;
    }
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory.get(slot);
    }
    public ItemEnchantmentsComponent getEnchantments() { return this.enchantments; }


    @Override
    public int getSize() {
        return this.inventory.size();
    }
}
