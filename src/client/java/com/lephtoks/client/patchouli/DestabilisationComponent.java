package com.lephtoks.client.patchouli;

import com.google.gson.annotations.SerializedName;
import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.client.utils.RenderUtils;
import com.lephtoks.enchantments.TaintedEnchantments;
import com.lephtoks.recipes.NBTIngredient;
import com.lephtoks.recipes.destabilisation.DestabilisationRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.function.UnaryOperator;

public class DestabilisationComponent implements ICustomComponent {
    protected transient NBTIngredient ingredient;
    protected transient ItemStack result;
    protected transient ItemStack table;
    protected transient int x, y;
    protected transient int power;
    @SerializedName("recipe_name")
    public String recipeName;

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, RegistryWrapper.WrapperLookup registries) {
        recipeName = lookup.apply(IVariable.wrap(recipeName, registries)).asString();
    }

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        if (recipeName.isEmpty()) return;
        this.x = componentX != -1 ? componentX : 17;
        this.y = componentY;
        World world = MinecraftClient.getInstance().world;
        assert world != null;
        this.ingredient = makeIngredient(world);
        this.result = makeResult(world);
        this.power = makePower(world);

        this.table = new ItemStack(TaintedBlocks.TAINTED_TABLE);
        RegistryEntry<Enchantment> enchantment = world.getRegistryManager().createRegistryLookup().getOptional(TaintedEnchantments.DESTABILISATION.getRegistryRef()).flatMap(optional -> optional.getOptional(TaintedEnchantments.DESTABILISATION)).orElse(null);
        var builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        builder.set(enchantment, power);
        this.table.set(DataComponentTypes.ENCHANTMENTS, builder.build());
    }

    protected NBTIngredient makeIngredient(World world) {
        var o = world.getRecipeManager().get(Identifier.of(recipeName));
        return o.map(recipeEntry -> ((DestabilisationRecipe) recipeEntry.value()).getIngredient()).orElse(null);
    }
    protected int makePower(World world) {
        var o = world.getRecipeManager().get(Identifier.of(recipeName));
        return o.map(recipeEntry -> ((DestabilisationRecipe) recipeEntry.value()).getCost()).orElse(0);
    };
    protected ItemStack makeResult(World world) {
        var o = world.getRecipeManager().get(Identifier.of(recipeName));
        if (o.isPresent()) {
            return o.get().value().getResult(world.getRegistryManager());
        }
        return ItemStack.EMPTY;
    };

    @Override
    public void render(DrawContext gui, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if (recipeName.isEmpty()) return;
        MatrixStack ms = gui.getMatrices();

        ms.push();
        context.renderItemStack(gui, x+32, y, mouseX, mouseY, this.table);
        RenderUtils.renderNBTIngredient(context, gui, x-10, y, mouseX, mouseY, this.ingredient);
        context.renderItemStack(gui, x + 74, y, mouseX, mouseY, this.result);


        RenderSystem.enableBlend();

        gui.drawTexture(context.getCraftingTexture(), x-13, y-3, 12, 72, 36, 24, 128, 256);
        RenderUtils.renderCraftingElement(x+57, y-4, gui, context.getCraftingTexture(), RenderUtils.CraftingAtlas.RIGHT_CELL);
        ms.pop();
    }

}
