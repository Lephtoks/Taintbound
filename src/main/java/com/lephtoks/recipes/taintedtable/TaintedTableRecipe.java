package com.lephtoks.recipes.taintedtable;

import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.recipes.NBTIngredient;
import com.lephtoks.recipes.TaintboundRecipes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;

import java.util.List;

public class TaintedTableRecipe extends AbstractTaintedTableRecipe {
    public TaintedTableRecipe(String group, ItemEnchantmentsComponent enchantments, ItemEnchantmentsComponent result, List<NBTIngredient> ingredient) {
        super(TaintboundRecipes.TET_RECIPE, TaintboundRecipes.TET_SERIALIZER, group, enchantments, result, ingredient);
    }

    public ItemStack createIcon() {
        return new ItemStack(TaintedBlocks.TAINTED_TABLE);
    }
}
