package com.lephtoks.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record OnePropertiedItemRecipeInput(ItemStack item, int property) implements RecipeInput {

    public ItemStack getStackInSlot(int slot) {
        if (slot != 0) {
            throw new IllegalArgumentException("No item for index " + slot);
        } else {
            return this.item;
        }
    }

    public int getSize() {
        return 1;
    }
}
