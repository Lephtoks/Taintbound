package com.lephtoks.client.gui.challenges;

import com.lephtoks.MCAccessor;
import com.lephtoks.client.gui.CustomTooltip;
import com.lephtoks.components.ChallengeComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class ChallengeTooltipComponent extends CustomTooltip<ChallengeComponent> {
    private final float percent;
    private static final int WIDTH = 100;
    private static final int Y_OFFSET = -4;
    public final ChallengeComponent component;
    private final ChallengeTypeTooltipRender render;

//    private int mouseX, mouseY;

    public ChallengeTooltipComponent(ChallengeComponent component) {
        super(component);
        this.component = component;
        this.render = ChallengeTypeTooltipRenders.get(component.type().getChallengeTooltipRenderIdentifier());
        this.percent = component.current() / component.max();
    }

    @Override
    public int getHeight() {
        return 40;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        String progressText = String.valueOf((int) this.component.max());
        int textWidth = textRenderer.getWidth(progressText);
        return WIDTH + 3 + textWidth;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int tick = ((MCAccessor) mc).getTick();

        String progressText = String.valueOf((int) this.component.current());
        float textWidth = textRenderer.getWidth(progressText);
        int color = getGradientColour((float) Math.sin(tick * 0.1)*0.5f + 0.5f);
        textRenderer.draw(progressText, Math.clamp(MathHelper.lerp(percent, x, x+WIDTH)-textWidth*0.5f, x + 3, ((int) (WIDTH - textWidth + x)) - 3), y - Y_OFFSET + 2, color, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        textRenderer.draw(String.valueOf((int) Math.ceil(this.component.max())), x + WIDTH + 3, y-1, color, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);

        textRenderer.draw(render.getSpecialText(this), x + 27, y + 22, 0xFFFFFFFF, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int tick = ((MCAccessor) mc).getTick();

        MatrixStack ms = context.getMatrices();
        int height = 3;
        ms.push();

        int manaBarWidth = (int) Math.ceil(WIDTH * percent);


        float delta = (float) Math.sin(tick * 0.5 * percent)*0.5f + 0.5f;
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        int startY = y - height - Y_OFFSET;
        int endX = x + manaBarWidth;
        int endY = y - Y_OFFSET;
        int colorStart = getGradientColour(delta);
        int colorEnd = getGradientColour(1-delta);


        float dividerD = (float) (Math.sin(tick * 0.2 * percent)*0.5f + 0.5f);
        int dividerX = MathHelper.lerp(dividerD, x, endX);

        drawGradient(ms, vertexConsumer, x, startY, dividerX, endY, colorStart, colorEnd);
        drawGradient(ms, vertexConsumer, dividerX, startY, endX, endY, colorEnd, colorStart);
        
        context.fill(x + manaBarWidth, y - height - Y_OFFSET, x + WIDTH, y - Y_OFFSET, 0xFF555555);

        this.render.renderSpecial(this, textRenderer, context, x+10, y+35, tick);

        ms.pop();
    }
    public static int getGradientColour(float delta) {
        return ColorHelper.Argb.lerp(delta, 0xFFc300ad, 0xFFe2ad3d);
    }
    private void drawGradient(MatrixStack ms, VertexConsumer vertexConsumer, int x, int startY, int endX, int endY, int colorStart, int colorEnd) {
        Matrix4f matrix4f = ms.peek().getPositionMatrix();
        vertexConsumer.vertex(matrix4f, (float) x, (float)startY, 0).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float) x, (float)endY, 0).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, 0).color(colorEnd);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, 0).color(colorEnd);
    }
}
