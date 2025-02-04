package com.lephtoks.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public interface NBTIngredient {
    public static final Codec<NBTIngredient> DISALLOW_EMPTY_CODEC = Codec.xor(ItemStack.UNCOUNTED_CODEC, Ingredient.DISALLOW_EMPTY_CODEC).xmap((either) -> either.map(ItemstackNBTIngredient::new, IngredientNBTIngredient::new), (entry) -> {
        if (entry instanceof ItemstackNBTIngredient i) {
            return Either.left(i.getItemStack());
        } else if (entry instanceof IngredientNBTIngredient i) {
            return Either.right(i.getIngredient());
        }
        return null;
    });
//    public static final Codec<NBTIngredient> ALLOW_EMPTY_CODEC;

    public Ingredient intoIngredient();
    public boolean test(ItemStack itemStack);
        // TODO: Переделать класс NBTIngredient в интерфейс и сделать два новых класса

//        ALLOW_EMPTY_CODEC = Codec.either(ItemStack.UNCOUNTED_CODEC, Ingredient.ALLOW_EMPTY_CODEC).flatComapMap((either) -> {
//            return (NBTIngredient)either.map(NBTIngredient::new, NBTIngredient::new);
//        },  (ingredient) -> {
//            if (ingredient.isNBT) {
//                return DataResult.success(Either.left(ingredient.itemStack));
//            } else {
//                return DataResult.success(Either.right(ingredient.ingredient));
//            }
//        });

    public boolean isEmpty();
}
