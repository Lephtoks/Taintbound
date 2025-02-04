package com.lephtoks.client.gui.screens;

import com.lephtoks.MCAccessor;
import com.lephtoks.TaintboundMod;
import com.lephtoks.blockentities.HollowCoreBlockEntity;
import com.lephtoks.client.utils.RenderUtils;
import com.lephtoks.recipes.OnePropertiedItemRecipeInput;
import com.lephtoks.recipes.TaintboundRecipes;
import com.lephtoks.screen.HollowCoreScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HollowCoreScreen extends HandledScreen<HollowCoreScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of(TaintboundMod.MOD_ID, "textures/gui/container/hollow_core.png");
    private static final Identifier DARK_BAR = Identifier.of(TaintboundMod.MOD_ID, "container/hollow_core_bar");
    private static final Identifier LIGHT_BAR = Identifier.of(TaintboundMod.MOD_ID, "container/hollow_core_bar_light");

    public HollowCoreScreen(HollowCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
        MinecraftClient mc = MinecraftClient.getInstance();

        float percent = handler != null ? handler.getPercent() : 0;
        float predictionPercent = handler != null ? handler.predictPercent() : 0;

        float abs_prediction = Math.abs(predictionPercent);
        float abs_percent = Math.abs(percent);

        Identifier main_texture = percent >= 0 ? LIGHT_BAR : DARK_BAR;
        Identifier second_texture = percent >= 0 ? DARK_BAR : LIGHT_BAR;


        if (Math.signum(percent) == Math.signum(predictionPercent) || Math.signum(percent) == 0 || Math.signum(predictionPercent) == 0) {
            if (abs_prediction > abs_percent) {
                // |#####.####----|
                context.drawGuiTexture(main_texture, 153, 5, 0, 0, x + 12, y + 21, (int) (153 * abs_percent), 5);
                RenderUtils.drawGuiTexture(context, main_texture, 153, 5, 0, 0, x + 12, y + 21, 1, (int) (153 * abs_prediction), 5, 1, 1, 1, ((float) Math.sin(((MCAccessor) mc ).getTick()*0.1f))*0.25f + 0.5f);
            } else if (abs_prediction < abs_percent) {
                // |####(#####)----|
                context.drawGuiTexture(main_texture, 153, 5, 0, 0, x + 12, y + 21, (int) (153 * abs_prediction), 5);
                RenderUtils.drawGuiTexture(context, main_texture, 153, 5, 0, 0, x + 12, y + 21, 1, (int) (153 * abs_percent), 5, 1, 1, 1, ((float) Math.sin(((MCAccessor) mc ).getTick()*0.3))*0.25f + 0.75f);
            } else {
                context.drawGuiTexture(main_texture, 153, 5, 0, 0, x + 12, y + 21, (int) (153 * abs_percent), 5);
            }
        } else {
                context.drawGuiTexture(main_texture, 153, 5, 0, 0, x + 12, y + 21, 1, (int) (153 * abs_percent), 5);
                RenderUtils.drawGuiTexture(context, second_texture, 153, 5, 0, 0, x + 12, y + 21, 2, (int) (153 * abs_prediction), 5, 1, 1, 1, ((float) Math.sin(((MCAccessor) mc ).getTick()*0.1))*0.1f + 0.3f);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        if (handler.isCrafting()) {
            Slot slot = handler.getSlot(0);
            int x1 = slot.x + this.x;
            int y1 = slot.y + this.y;
            context.enableScissor(x1, y1+((int)(16*handler.craftingProgress())), x1+16, y1+16);
            context.drawItem(slot.getStack(), x1, y1);
            context.disableScissor();
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                context.enableScissor(x1, y1, x1+16, y1+((int)(16*handler.craftingProgress())));
                world.getRecipeManager().getFirstMatch(TaintboundRecipes.HC_RECIPE, new OnePropertiedItemRecipeInput(slot.getStack(), (int) (HollowCoreBlockEntity.MAX_ENERGY * handler.getPercent())), world).map(RecipeEntry::value).map((v) -> v.getResult(world.getRegistryManager())).ifPresent(itemStack -> context.drawItem(itemStack, x1, y1));
                context.disableScissor();
            }
        }

        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
