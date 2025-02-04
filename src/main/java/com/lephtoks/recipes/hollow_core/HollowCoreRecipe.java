package com.lephtoks.recipes.hollow_core;

import com.lephtoks.TaintboundMod;
import com.lephtoks.blocks.AbstractHollowCore;
import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.recipes.NBTIngredient;
import com.lephtoks.recipes.OnePropertiedItemRecipeInput;
import com.lephtoks.recipes.SingleSlotPropertiedRecipe;
import com.lephtoks.recipes.TaintboundRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HollowCoreRecipe extends SingleSlotPropertiedRecipe {
    public HollowCoreRecipe(String group, NBTIngredient ingredient, ItemStack result, int difficulty) {
        super(TaintboundRecipes.HC_RECIPE, TaintboundRecipes.HC_SERIALIZER, group, ingredient, result, difficulty);
    }
    public static Identifier TYPE_ID = Identifier.of(TaintboundMod.MOD_ID, "hollow_core_infusion");

    public ItemStack createIcon() {
        return new ItemStack(TaintedBlocks.HOLLOW_CORE);
    }

    @Override
    public boolean matches(OnePropertiedItemRecipeInput input, World world) {
        return AbstractHollowCore.energyQMoreThan(input.property(), cost) && ingredient.test(input.getStackInSlot(0));
    }
}
