package com.lephtoks.items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static com.lephtoks.TaintboundMod.MOD_ID;

public class TaintedItems {
    public static Item register(String id, Item item) {

        Identifier itemID = Identifier.of(MOD_ID, id);

        return Registry.register(Registries.ITEM, itemID, item);
    }
    public static final Item CHAOTIC_DUST = register("chaotic_dust", new ChaoticDust(new Item.Settings()));
    public static final Item DARK_GRIMOIRE = register("dark_grimoire", new DarkGrimoire(new Item.Settings()));
    public static final Item HOLLOW_CRYSTAL = register("hollow_crystal", new Item(new Item.Settings().maxCount(8)));
    public static final Item POSITIVE_CRYSTAL = register("positive_crystal", new Item(new Item.Settings().fireproof().maxCount(8).rarity(Rarity.EPIC)));
    public static final Item NEGATIVE_CRYSTAL = register("negative_crystal", new Item(new Item.Settings().maxCount(8).rarity(Rarity.EPIC)));

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
            itemGroup.addAfter(Items.DIAMOND, HOLLOW_CRYSTAL, POSITIVE_CRYSTAL, NEGATIVE_CRYSTAL);
            itemGroup.addAfter(Items.GUNPOWDER, CHAOTIC_DUST);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> {
            itemGroup.addAfter(Items.WRITABLE_BOOK, DARK_GRIMOIRE);
        });
    }
}
