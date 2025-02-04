package com.lephtoks.client.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class TaintboundEMIRecipe<T extends Recipe<?>> implements EmiRecipe {
    protected final T recipe;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final Identifier id;

    public TaintboundEMIRecipe(RecipeEntry<T> recipe) {
        this.recipe = recipe.value();
        this.inputs = this.recipe.getIngredients().stream().map(EmiIngredient::of).toList();
        this.outputs = List.of(EmiStack.of(this.recipe.getResult(null)));
        this.id = recipe.id();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }
}
