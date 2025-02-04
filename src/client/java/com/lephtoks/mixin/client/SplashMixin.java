package com.lephtoks.mixin.client;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(SplashTextResourceSupplier.class)
public class SplashMixin {
    @Final
    @Shadow
    private List<String> splashTexts;
    @Inject(at = @At("TAIL"), method = "apply")
    private void set(List<String> list, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        this.splashTexts.add("§dInspired by TBOI!");
    }
}