package com.lephtoks.client.patchouli;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import com.lephtoks.recipes.NBTIngredient;
import com.lephtoks.recipes.taintedtable.TaintedTableRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IVariable;

import java.util.List;
import java.util.function.UnaryOperator;

public class TableCraftComponent extends AbstractTableCraftComponent {
    @SerializedName("recipe_name")
    public String recipeName;

    @Override
    protected List<NBTIngredient> makeIngredients() {
        World world = MinecraftClient.getInstance().world;
        assert world != null;
        var o = world.getRecipeManager().get(Identifier.of(recipeName));
        if (o.isPresent()) {
            return ((TaintedTableRecipe)o.get().value()).getAdvIngredients();
        }
        return ImmutableList.of();
    }
    @Override
    protected ItemEnchantmentsComponent makeREnchantments() {
        World world = MinecraftClient.getInstance().world;
        assert world != null;
        var o = world.getRecipeManager().get(Identifier.of(recipeName));
        if (o.isPresent()) {
            return ((TaintedTableRecipe) o.get().value()).getResultEnchant();
        }
        return ItemEnchantmentsComponent.DEFAULT;
    }
    @Override
    protected ItemEnchantmentsComponent makeIEnchantments() {
        World world = MinecraftClient.getInstance().world;
        assert world != null;
        var o = world.getRecipeManager().get(Identifier.of(recipeName));
        if (o.isPresent()) {
            return ((TaintedTableRecipe) o.get().value()).getInputEnchantments();
        }
        return ItemEnchantmentsComponent.DEFAULT;
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, RegistryWrapper.WrapperLookup registries) {
        recipeName = lookup.apply(IVariable.wrap(recipeName, registries)).asString();
    }
}
