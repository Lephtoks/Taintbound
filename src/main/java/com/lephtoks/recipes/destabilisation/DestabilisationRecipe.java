package com.lephtoks.recipes.destabilisation;

import com.lephtoks.TaintboundMod;
import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.recipes.NBTIngredient;
import com.lephtoks.recipes.OnePropertiedItemRecipeInput;
import com.lephtoks.recipes.SingleSlotPropertiedRecipe;
import com.lephtoks.recipes.TaintboundRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DestabilisationRecipe extends SingleSlotPropertiedRecipe {
    public DestabilisationRecipe(String group, NBTIngredient ingredient, ItemStack result, int difficulty) {
        super(TaintboundRecipes.DESTABILISATION_RECIPE, TaintboundRecipes.DESTABILISATION_SERIALIZER, group, ingredient, result, difficulty);
    }

    @Override
    public boolean matches(OnePropertiedItemRecipeInput input, World world) {
        return input.property() == this.cost && ingredient.test(input.getStackInSlot(0));
    }

    public static Identifier TYPE_ID = Identifier.of(TaintboundMod.MOD_ID, "destabilisation");

    public ItemStack createIcon() {
        return new ItemStack(TaintedBlocks.TAINTED_TABLE);
    }
}
