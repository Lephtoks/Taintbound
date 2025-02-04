package com.lephtoks.mixin;

import com.lephtoks.registries.TaintedRegistries;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registries.class)
public class RegistriesMixin {
    @Inject(at = @At(value = "TAIL", by=-5, shift = At.Shift.BY), method = "<clinit>")
    private static void createRegs(CallbackInfo callbackInfo) {
        TaintedRegistries.init();
    }
}
