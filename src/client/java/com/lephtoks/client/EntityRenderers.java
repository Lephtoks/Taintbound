//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.lephtoks.client;

import com.lephtoks.blockentities.TaintedBlockEntityTypes;
import com.lephtoks.client.blockentities.HollowCoreBlockEntityRender;
import com.lephtoks.client.blockentities.HollowCoreWithDaylightSensorBlockEntityRender;
import com.lephtoks.client.blockentities.TaintedTableBlockEntityRender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class EntityRenderers {
    public static void initialize() {
        BlockEntityRendererFactories.register(TaintedBlockEntityTypes.TAINTED_TABLE, TaintedTableBlockEntityRender::new);

        BlockEntityRendererFactories.register(TaintedBlockEntityTypes.HOLLOW_CORE, HollowCoreBlockEntityRender::new);
        BlockEntityRendererFactories.register(TaintedBlockEntityTypes.HOLLOW_CORE_WITH_DAYLIGHT_SENSOR, HollowCoreWithDaylightSensorBlockEntityRender::new);

    }
}
