package com.lephtoks.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class ItemstackNBTIngredient implements NBTIngredient {
    ItemStack itemStack;
    public ItemstackNBTIngredient(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public Ingredient intoIngredient() {
        return Ingredient.ofStacks(itemStack);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ItemStack.areItemsAndComponentsEqual(this.itemStack, itemStack);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean isEmpty() {
        return itemStack.isEmpty();
    }
}
