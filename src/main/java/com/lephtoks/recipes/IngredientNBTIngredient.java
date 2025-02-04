package com.lephtoks.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;

public class IngredientNBTIngredient implements NBTIngredient {
    Ingredient ingredient;
    public IngredientNBTIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public Ingredient intoIngredient() {
        return ingredient;
    }

    @Override
    public boolean test(@Nullable ItemStack itemStack) {
        return this.ingredient.test(itemStack);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public boolean isEmpty() {
        return ingredient.isEmpty();
    }
}
