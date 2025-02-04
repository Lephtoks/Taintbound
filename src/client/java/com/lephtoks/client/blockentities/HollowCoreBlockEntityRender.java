package com.lephtoks.client.blockentities;

import com.lephtoks.TaintboundMod;
import com.lephtoks.blockentities.HollowCoreBlockEntity;
import com.lephtoks.blocks.AbstractHollowCore;
import com.lephtoks.client.gui.challenges.ChallengeTooltipComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

import static org.joml.Math.toRadians;

@Environment(EnvType.CLIENT)
public class HollowCoreBlockEntityRender implements BlockEntityRenderer<HollowCoreBlockEntity> {
    public static final EntityModelLayer LAYER = create();
    public static final SpriteIdentifier CUBE_TEXTURE_DISABLED = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of(TaintboundMod.MOD_ID, "block/core_disabled"));
    public static final SpriteIdentifier CUBE_TEXTURE_ENABLED = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of(TaintboundMod.MOD_ID, "block/tainted_bookshelf"));

    private static EntityModelLayer create() {
        var a =new EntityModelLayer(Identifier.of(TaintboundMod.MOD_ID, "hollow_core"), "main");
        ;if (!EntityModelLayers.LAYERS.add(a)) {
            throw new IllegalStateException("Duplicate registration for " + String.valueOf(a));
        } else {
            return a;
        }
    }
    private final ModelPart cube;

    public HollowCoreBlockEntityRender(BlockEntityRendererFactory.Context ctx) {
        this.cube = ctx.getLayerModelPart(LAYER);
    }

    @Override
    public void render(HollowCoreBlockEntity entity, float tickDelta, MatrixStack ms, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient mc = MinecraftClient.getInstance();
        float t = entity.ticks + tickDelta;

        ms.push();

        ms.translate(0.5f, 0.5f, 0.5f);
        if (mc.world != null) {
            float x = (float) toRadians(Math.sin(t * 0.01f) * 20);
            float y = toRadians(t * 5);
            float z = toRadians(t * 0.5f);

            ms.multiply(new Quaternionf().rotateXYZ(x, y, z));

            VertexConsumer vertexConsumer = CUBE_TEXTURE_DISABLED.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
            this.cube.render(ms, vertexConsumer, light, overlay);
        }
        ms.pop();
        if (mc.world != null && mc.player != null) {
            BlockState blockState = mc.world.getBlockState(entity.getPos());
            if (blockState.getBlock() instanceof AbstractHollowCore block && block.isWorking(mc.world, blockState)) {
                float d = Math.abs(
                        (
                                Math.abs(
                                        ((float) mc.world.getTimeOfDay())
                                                - 6000f
                                )
                                        % 24000f
                                        - 12000f
                        )
                ) / 12000f;
                BeaconBlockEntityRenderer.renderBeam(ms, vertexConsumers, BeaconBlockEntityRenderer.BEAM_TEXTURE, tickDelta, 1F, entity.ticks, 1, BeaconBlockEntityRenderer.MAX_BEAM_HEIGHT,
                        ChallengeTooltipComponent.getGradientColour(d), 0.2f, 0.25f);
            }
        }
    }

}
