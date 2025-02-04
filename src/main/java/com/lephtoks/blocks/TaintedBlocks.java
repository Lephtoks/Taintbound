package com.lephtoks.blocks;

import com.lephtoks.TaintboundMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class TaintedBlocks {
    public static final Block TAINTED_TABLE = register(
            new TaintedTable(AbstractBlock.Settings.copy(Blocks.ENCHANTING_TABLE)),
            "tainted_table"
    );
    public static final Block TAINTED_BOOKSHELF = register(new Block(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASS).strength(1.5F).sounds(BlockSoundGroup.WOOD).burnable()), "tainted_bookshelf");
    public static final Block HOLLOW_CORE = register(
            new HollowCore(AbstractBlock.Settings.copy(Blocks.BEACON)),
            "hollow_core"
    );
    public static final Block HOLLOW_CORE_WITH_DAYLIGHT_SENSOR = register(
            new HollowCoreWithSolarPanel(AbstractBlock.Settings.copy(HOLLOW_CORE)),
            "hollow_core_with_daylight_sensor"
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.addAfter(Blocks.ENCHANTING_TABLE, TaintedBlocks.TAINTED_TABLE.asItem());
            itemGroup.addAfter(Blocks.BOOKSHELF, TaintedBlocks.TAINTED_BOOKSHELF.asItem());
            itemGroup.addAfter(TAINTED_TABLE, HOLLOW_CORE);
            itemGroup.addAfter(HOLLOW_CORE, HOLLOW_CORE_WITH_DAYLIGHT_SENSOR);
        });
    }
    public static Block register(Block block, String name) {
        return register(block, name, true);
    }

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        // Register the block and its item.
        Identifier id = Identifier.of(TaintboundMod.MOD_ID, name);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings().rarity(Rarity.UNCOMMON));
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }
}
