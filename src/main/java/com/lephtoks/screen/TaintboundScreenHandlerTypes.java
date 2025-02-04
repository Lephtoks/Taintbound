package com.lephtoks.screen;

import com.lephtoks.TaintboundMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class TaintboundScreenHandlerTypes {

    public static final ScreenHandlerType<HollowCoreScreenHandler> HOLLOW_CORE = register("hollow_core", HollowCoreScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(TaintboundMod.MOD_ID, id), new ScreenHandlerType<>(factory, FeatureSet.empty()));
    }
}
