package com.lephtoks.client.blockentities;

import com.lephtoks.blockentities.HollowCoreBlockEntity;
import com.lephtoks.blocks.HollowCoreWithSolarPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;

import static org.joml.Math.toRadians;

@Environment(EnvType.CLIENT)
public class HollowCoreWithDaylightSensorBlockEntityRender extends HollowCoreBlockEntityRender {
    private static final BlockState DAY = Blocks.DAYLIGHT_DETECTOR.getDefaultState();
    private static final BlockState NIGHT = Blocks.DAYLIGHT_DETECTOR.getDefaultState().cycle(DaylightDetectorBlock.INVERTED);
    public HollowCoreWithDaylightSensorBlockEntityRender(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(HollowCoreBlockEntity entity, float tickDelta, MatrixStack ms, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient mc = MinecraftClient.getInstance();
        float t = entity.ticks + tickDelta;

        if (mc.world != null) {
            ms.push();

            ms.translate(0.5f, 1.01, 0.5f);
            float x = (float) toRadians(Math.sin(t * 0.01f) * 5);
            float y = toRadians(t * 0.1f);
            float z = -x;

            ms.multiply(new Quaternionf().rotateXYZ(x, y, z));

            ms.scale(0.4f, 0.4f, 0.4f);

            BakedModel model;
            BlockState blockState = mc.world.getBlockState(entity.getPos());
            if (blockState.contains(HollowCoreWithSolarPanel.INVERTED) && blockState.get(HollowCoreWithSolarPanel.INVERTED)) {
                model = mc.getBlockRenderManager().getModel(NIGHT);
            } else {
                model = mc.getBlockRenderManager().getModel(DAY);
            }
            mc.getItemRenderer().renderItem(Blocks.DAYLIGHT_DETECTOR.asItem().getDefaultStack(), ModelTransformationMode.NONE, false, ms, vertexConsumers, light, overlay, model);

            ms.pop();
        }

        super.render(entity, tickDelta, ms, vertexConsumers, light, overlay);
    }

}
