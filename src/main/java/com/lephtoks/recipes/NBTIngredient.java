package com.lephtoks.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public interface NBTIngredient {
    Codec<NBTIngredient> DISALLOW_EMPTY_CODEC = Codec.xor(ItemStack.UNCOUNTED_CODEC, Ingredient.DISALLOW_EMPTY_CODEC).xmap((either) -> either.map(ItemstackNBTIngredient::new, IngredientNBTIngredient::new), (entry) -> {
        if (entry instanceof ItemstackNBTIngredient i) {
            return Either.left(i.getItemStack());
        } else if (entry instanceof IngredientNBTIngredient i) {
            return Either.right(i.getIngredient());
        }
        return null;
    });

    Ingredient intoIngredient();
    boolean test(ItemStack itemStack);

    boolean isEmpty();
}
