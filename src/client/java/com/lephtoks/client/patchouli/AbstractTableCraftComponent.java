package com.lephtoks.client.patchouli;

import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.recipes.IngredientNBTIngredient;
import com.lephtoks.recipes.ItemstackNBTIngredient;
import com.lephtoks.recipes.NBTIngredient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;

import java.util.List;

abstract class AbstractTableCraftComponent implements ICustomComponent {
    protected transient List<NBTIngredient> ingredients;
    protected transient ItemEnchantmentsComponent enchantments;
    protected transient ItemEnchantmentsComponent resultEnchantments;
    protected transient ItemStack table;
    protected transient ItemStack sculk;
    protected transient int x, y;
    protected int rx, ry;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = componentX != -1 ? componentX : 17;
        this.y = componentY;
        this.ingredients = makeIngredients();
        this.enchantments = makeIEnchantments();
        this.resultEnchantments = makeREnchantments();
        this.table = new ItemStack(TaintedBlocks.TAINTED_TABLE);
        this.table.set(DataComponentTypes.ENCHANTMENTS, enchantments);

        this.sculk = new ItemStack(Blocks.SCULK);
        this.sculk.set(DataComponentTypes.ENCHANTMENTS, resultEnchantments);
    }

    protected abstract List<NBTIngredient> makeIngredients();
    protected abstract ItemEnchantmentsComponent makeREnchantments();
    protected abstract ItemEnchantmentsComponent makeIEnchantments();

    @Override
    public void render(DrawContext gui, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        int degreePerInput = (int) (360F / ingredients.size());
        int ticksElapsed = context.getTicksInBook();

        float currentDegree = Screen.hasShiftDown()
                        ? ticksElapsed
                        : ticksElapsed + pticks;

        for (NBTIngredient input : ingredients) {
            renderNBTIngredientAtAngle(gui, context, currentDegree, input, mouseX, mouseY);

            currentDegree += degreePerInput;
        }

        MatrixStack ms = gui.getMatrices();

        ms.push();
        context.renderItemStack(gui, x+32, y+32, mouseX, mouseY, this.table);
        context.renderItemStack(gui, rx, ry, mouseX, mouseY, this.sculk);


        RenderSystem.enableBlend();
        gui.drawTexture(context.getCraftingTexture(), rx-17, ry-4, 70, 71, 37, 24, 128, 256);

        ms.pop();
    }

    private void renderNBTIngredientAtAngle(DrawContext gui, IComponentRenderContext context, float angle, NBTIngredient ingredient, int mouseX, int mouseY) {
        MatrixStack ms = gui.getMatrices();
        if (ingredient.isEmpty()) {
            return;
        }

        angle -= 90;
        int radius = 44;
        double xPos = x + Math.cos(angle * Math.PI / 180D) * radius + 32;
        double yPos = y + Math.sin(angle * Math.PI / 180D) * radius + 32;

        ms.push();
        ms.translate(xPos - (int) xPos, yPos - (int) yPos, 0);

        if (ingredient instanceof ItemstackNBTIngredient i) {
            context.renderItemStack(gui, (int) xPos, (int) yPos, mouseX, mouseY, i.getItemStack());
        } else if (ingredient instanceof IngredientNBTIngredient i) {
            context.renderIngredient(gui, (int) xPos, (int) yPos, mouseX, mouseY, i.getIngredient());
        }
        ms.pop();
    }

}
