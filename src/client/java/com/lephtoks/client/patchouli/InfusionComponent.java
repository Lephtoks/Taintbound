package com.lephtoks.client.patchouli;

import com.google.gson.annotations.SerializedName;
import com.lephtoks.blockentities.HollowCoreBlockEntity;
import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.client.utils.RenderUtils;
import com.lephtoks.recipes.NBTIngredient;
import com.lephtoks.recipes.hollow_core.HollowCoreRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.function.UnaryOperator;

public class InfusionComponent implements ICustomComponent {
    protected transient NBTIngredient ingredient;
    protected transient ItemStack result;
    protected transient int x, y;
    protected transient float progress;
    protected transient boolean isDark;
    private static final Identifier EMPTY_BAR = Identifier.of("taintbound", "textures/gui/sprites/container/hollow_core_emi_empty.png");
    private static final Identifier LIGHT_BAR = Identifier.of("taintbound", "textures/gui/sprites/container/hollow_core_bar_emi_light.png");
    private static final Identifier DARK_BAR = Identifier.of("taintbound", "textures/gui/sprites/container/hollow_core_bar_emi.png");
    private static final ItemStack HOLLOW_CORE = TaintedBlocks.HOLLOW_CORE.asItem().getDefaultStack();
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
        int cost = makeCost(world);
        this.progress = Math.abs(cost) / HollowCoreBlockEntity.MAX_ENERGY;
        this.isDark = cost <= 0;
    }

    protected NBTIngredient makeIngredient(World world) {
        var o = world.getRecipeManager().get(Identifier.of(recipeName));
        return o.map(recipeEntry -> ((HollowCoreRecipe) recipeEntry.value()).getIngredient()).orElse(null);
    }
    protected int makeCost(World world) {
        var o = world.getRecipeManager().get(Identifier.of(recipeName));
        return o.map(recipeEntry -> ((HollowCoreRecipe) recipeEntry.value()).getCost()).orElse(0);
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
        RenderUtils.renderNBTIngredient(context, gui, x-10, y, mouseX, mouseY, this.ingredient);
        context.renderItemStack(gui, x + 74, y, mouseX, mouseY, this.result);

        context.renderItemStack(gui, x + 32, y, mouseX, mouseY, HOLLOW_CORE);

        RenderSystem.enableBlend();

        gui.drawTexture(EMPTY_BAR, x-10, y-11, 0, 0, 100, 5, 100, 5);

        gui.drawTexture(isDark ? DARK_BAR : LIGHT_BAR, x-10, y-11, 0, 0, (int) (100*this.progress), 5, 100, 5);

        gui.drawTexture(context.getCraftingTexture(), x-13, y-3, 12, 72, 36, 24, 128, 256);
        RenderUtils.renderCraftingElement(x+57, y-4, gui, context.getCraftingTexture(), RenderUtils.CraftingAtlas.RIGHT_CELL);
        ms.pop();
    }

}
