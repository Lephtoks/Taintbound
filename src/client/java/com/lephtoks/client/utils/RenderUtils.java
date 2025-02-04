package com.lephtoks.client.utils;

import com.lephtoks.recipes.IngredientNBTIngredient;
import com.lephtoks.recipes.ItemstackNBTIngredient;
import com.lephtoks.recipes.NBTIngredient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IComponentRenderContext;

@Environment(EnvType.CLIENT)
public class RenderUtils {
    public static void renderNBTIngredient(IComponentRenderContext context, DrawContext graphics, int x, int y, int mouseX, int mouseY, NBTIngredient ingredient) {
        if (ingredient instanceof ItemstackNBTIngredient i) {
            context.renderItemStack(graphics, x, y, mouseX, mouseY, i.getItemStack());
        } else if (ingredient instanceof IngredientNBTIngredient i) {
            context.renderIngredient(graphics, x, y, mouseX, mouseY, i.getIngredient());
        }
    }

    /**
     * In matrices must be pushed value
     * */
    public static void renderCraftingElement(int x, int y, DrawContext context, Identifier craftingTexture, CraftingAtlas element) {
        context.drawTexture(craftingTexture, x, y, element.u, element.v, element.h, element.w, 128, 256);
    }

    public static enum CraftingAtlas {
        RIGHT_CELL(70, 71, 37, 24),
        LEFT_CELL(0, 64, 11, 11);
        private final int u;
        private final int v;
        private final int h;
        private final int w;

        private CraftingAtlas(int u, int v, int h, int w) {
            this.u = u;
            this.v = v;
            this.h = h;
            this.w = w;
        }
    }

    public static void drawGuiTexture(DrawContext drawContext, Identifier texture, int i, int j, int k, int l, int x, int y, int z, int width, int height, float r, float g, float b, float a) {
        Sprite sprite = drawContext.guiAtlasManager.getSprite(texture);
        Scaling scaling = drawContext.guiAtlasManager.getScaling(sprite);
        if (scaling instanceof Scaling.Stretch) {
            RenderUtils.drawSprite(drawContext, sprite, i, j, k, l, x, y, z, width, height, r, g, b, a);
        } else {
            RenderUtils.drawSprite(drawContext, sprite, x, y, z, width, height, r, g, b, a);
        }

    }

    private static void drawSprite(DrawContext drawContext, Sprite sprite, int i, int j, int k, int l, int x, int y, int z, int width, int height, float r, float g, float b, float a) {
        if (width != 0 && height != 0) {
            drawContext.drawTexturedQuad(sprite.getAtlasId(), x, x + width, y, y + height, z, sprite.getFrameU((float)k / (float)i), sprite.getFrameU((float)(k + width) / (float)i), sprite.getFrameV((float)l / (float)j), sprite.getFrameV((float)(l + height) / (float)j), r, g, b, a);
        }
    }

    private static void drawSprite(DrawContext drawContext, Sprite sprite, int x, int y, int z, int width, int height, float r, float g, float b, float a) {
        if (width != 0 && height != 0) {
            drawContext.drawTexturedQuad(sprite.getAtlasId(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), r, g, b, a);
        }
    }
}
