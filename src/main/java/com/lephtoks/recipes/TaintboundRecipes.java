package com.lephtoks.recipes;

import com.lephtoks.recipes.destabilisation.DestabilisationRecipe;
import com.lephtoks.recipes.hollow_core.HollowCoreRecipe;
import com.lephtoks.recipes.taintedtable.AbstractTaintedTableRecipe;
import com.lephtoks.recipes.taintedtable.TaintedTableRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class TaintboundRecipes {
    public static RecipeType<TaintedTableRecipe> TET_RECIPE = Registry.register(Registries.RECIPE_TYPE, TaintedTableRecipe.TYPE_ID, new RecipeType<TaintedTableRecipe>() {});
    public static RecipeSerializer<TaintedTableRecipe> TET_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, TaintedTableRecipe.TYPE_ID, new AbstractTaintedTableRecipe.Serializer<>(TaintedTableRecipe::new));

    public static RecipeSerializer<HollowCoreRecipe> HC_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, HollowCoreRecipe.TYPE_ID, new HollowCoreRecipe.Serializer<>(HollowCoreRecipe::new));
    public static RecipeType<HollowCoreRecipe> HC_RECIPE = Registry.register(Registries.RECIPE_TYPE, HollowCoreRecipe.TYPE_ID, new RecipeType<HollowCoreRecipe>() {});

    public static RecipeType<DestabilisationRecipe> DESTABILISATION_RECIPE = Registry.register(Registries.RECIPE_TYPE, DestabilisationRecipe.TYPE_ID, new RecipeType<DestabilisationRecipe>() {});
    public static RecipeSerializer<DestabilisationRecipe> DESTABILISATION_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, DestabilisationRecipe.TYPE_ID, new SingleSlotPropertiedRecipe.Serializer<>(DestabilisationRecipe::new));

    public static void init() {

    }
}
