package com.lephtoks.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.lephtoks.client.blockentities.HollowCoreBlockEntityRender;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityModels.class)
public class EntityModelsMixin {
    @Unique
    private static TexturedModelData createModel() {
        ModelData modelData = new ModelData();
        modelData.getRoot();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-4, -4, -4, 8, 8, 8), ModelTransform.pivot(0, 0, 0));
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Inject(method = "getModels", at = @At(value = "HEAD", shift = At.Shift.BY, by = 5))
    private static void addModels(CallbackInfoReturnable<Map<EntityModelLayer, TexturedModelData>> cir, @Local(ordinal = 0) ImmutableMap.Builder builder) {
        builder.put(HollowCoreBlockEntityRender.LAYER, createModel());
    }
}
