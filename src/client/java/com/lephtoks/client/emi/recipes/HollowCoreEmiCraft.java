package com.lephtoks.client.emi.recipes;

import com.lephtoks.TaintboundMod;
import com.lephtoks.blockentities.HollowCoreBlockEntity;
import com.lephtoks.client.emi.EMIIntegration;
import com.lephtoks.recipes.hollow_core.HollowCoreRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

public class HollowCoreEmiCraft extends TaintboundEMIRecipe<HollowCoreRecipe> {
    private static final Identifier DARK_BAR = Identifier.of(TaintboundMod.MOD_ID, "textures/gui/sprites/container/hollow_core_bar_emi.png");
    private static final Identifier LIGHT_BAR = Identifier.of(TaintboundMod.MOD_ID, "textures/gui/sprites/container/hollow_core_bar_emi_light.png");
    private static final Identifier EMPTY_BAR = Identifier.of(TaintboundMod.MOD_ID, "textures/gui/sprites/container/hollow_core_emi_empty.png");
    public HollowCoreEmiCraft(RecipeEntry<HollowCoreRecipe> recipe) {
        super(recipe);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIIntegration.HOLLOW_CORE;
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
//        widgetHolder.addTexture(LIGHT_BAR, 10, 10, 50, 4, 0, 0);
//        widgetHolder.addTexture(DARK_BAR, 10, 20, 50, 3, 0, 0);
        float progress = recipe.getCost() / HollowCoreBlockEntity.MAX_ENERGY;
        widgetHolder.addTexture(EMPTY_BAR, 10, 5, 100, 5, 0, 0, 100, 5, 100, 5);
        int i = Math.abs((int) (100 * progress));

        Identifier bar = recipe.getCost() > 0 ? LIGHT_BAR : DARK_BAR;

        widgetHolder.addTexture(bar, 10, 5, i, 5, 0, 0, i, 5, 100, 5);

        widgetHolder.addSlot(this.getInputs().getFirst(), 20, 15+8).recipeContext(this);
        widgetHolder.addSlot(this.getOutputs().getFirst(), 68+4, 15+4).large(true).recipeContext(this);
        widgetHolder.addTexture(EmiTexture.EMPTY_ARROW, 43, 15+8);
    }
}
