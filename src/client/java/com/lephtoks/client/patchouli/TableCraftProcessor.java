package com.lephtoks.client.patchouli;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import static net.minecraft.util.Identifier.tryParse;

public class TableCraftProcessor implements IComponentProcessor {

    private Recipe<?> recipe;

    @Override
    public void setup(World level, IVariableProvider variables) {
        String recipeId = variables.get("recipe", level.getRegistryManager()).asString();
        RecipeManager manager = level.getRecipeManager();
        recipe = manager.get(tryParse(recipeId)).orElseThrow(IllegalArgumentException::new).value();
    }

    @Override
    public IVariable process(World level, String key) {
        return null;
    }
}
