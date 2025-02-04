package com.lephtoks.blockentities;

import com.lephtoks.TaintboundMod;
import com.lephtoks.blocks.TaintedBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TaintedBlockEntityTypes {

    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(TaintboundMod.MOD_ID, path), blockEntityType);
    }

    public static final BlockEntityType<TaintedTableBlockEntity> TAINTED_TABLE = register(
            "tainted_table",
            BlockEntityType.Builder.create(TaintedTableBlockEntity::new, TaintedBlocks.TAINTED_TABLE).build()
    );

    public static final BlockEntityType<HollowCoreBlockEntity> HOLLOW_CORE = register(
            "hollow_core",
            BlockEntityType.Builder.create((pos, state) -> new HollowCoreBlockEntity(TaintedBlockEntityTypes.HOLLOW_CORE, pos, state), TaintedBlocks.HOLLOW_CORE).build()
    );

    public static final BlockEntityType<HollowCoreBlockEntity> HOLLOW_CORE_WITH_DAYLIGHT_SENSOR = register(
            "hollow_core_with_daylight_sensor",
            BlockEntityType.Builder.create((pos, state) -> new HollowCoreBlockEntity(TaintedBlockEntityTypes.HOLLOW_CORE_WITH_DAYLIGHT_SENSOR, pos, state), TaintedBlocks.HOLLOW_CORE_WITH_DAYLIGHT_SENSOR).build()
    );

    public static void initialize() {
    }
}