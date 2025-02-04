package com.lephtoks.client.blockentities;

import com.lephtoks.blockentities.TaintedTableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static org.joml.Math.toRadians;

@Environment(EnvType.CLIENT)
public class TaintedTableBlockEntityRender implements BlockEntityRenderer<TaintedTableBlockEntity> {

    public TaintedTableBlockEntityRender(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(TaintedTableBlockEntity entity, float tickDelta, MatrixStack ms, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient mc = MinecraftClient.getInstance();
        float t = entity.ticks + tickDelta;

        ms.push();

        float x = (float) toRadians(Math.sin(t*0.01f)*20);
        float y = toRadians(5*t);
        float z = toRadians(t*0.5f);

        ms.translate(0.5F, 1.25F, 0.5F);
        ms.multiply(new Quaternionf().rotateXYZ(x, y, z));

        float scale = (float) (0.5f * (Math.abs(Math.sin(t*0.01)*0.15f) + 0.85f));

        ms.scale(scale, scale, scale);


        ItemStack stack = Blocks.SCULK.asItem().getDefaultStack();
        if (!entity.enchIsEmpty()) stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        mc.getItemRenderer().renderItem(stack, ModelTransformationMode.HEAD,
                light, overlay, ms, vertexConsumers, entity.getWorld(), 0);
        ms.pop();


        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : entity.getInventory()) {
            if (item != ItemStack.EMPTY) {
                items.add(item);
            }
        }

        double angle = 360D / items.size();
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            ms.push();
            ms.translate(0.5F, 1.25F, 0.5F);
            ms.multiply(new Quaternionf().rotateY(toRadians((float) (angle * i + t))));
            ms.translate(1.125F, 0F, 0.25F);
            ms.multiply(new Quaternionf().rotateY(toRadians(90)));
            ms.translate(0D, 0.075 * Math.sin((t + i * 10) / 5D), 0F);
            mc.getItemRenderer().renderItem(itemStack, ModelTransformationMode.GROUND,
                    light, overlay, ms, vertexConsumers, entity.getWorld(), 0);
            ms.pop();
        }


        entity.getStack(0).getTooltipData();
    }

}
