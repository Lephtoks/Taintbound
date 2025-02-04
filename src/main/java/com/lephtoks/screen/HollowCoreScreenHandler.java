package com.lephtoks.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class HollowCoreScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public HollowCoreScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(4));
    }

    public HollowCoreScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(TaintboundScreenHandlerTypes.HOLLOW_CORE, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
        inventory.onOpen(playerInventory.player);

        int k;
        int l;
        this.addSlot(new HollowCoreSlot(inventory, 0, 8 + 4 * 18, 36 + 7, propertyDelegate));

        for(k = 0; k < 3; ++k) {
            for(l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < this.inventory.size()) {
                if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;
    }
    public float getPercent() {
        return propertyDelegate.get(0) * 0.0001f;
    }
    public float predictPercent() {
        return propertyDelegate.get(1) * 0.0001f;
    }
    public boolean isCrafting() {
        return propertyDelegate.get(2) == 1;
    }
    public float craftingProgress() {
        return propertyDelegate.get(3) * 0.0001f;
    }

    private static class HollowCoreSlot extends Slot {
        private final PropertyDelegate propertyDelegate;

        public HollowCoreSlot(Inventory inventory, int index, int x, int y, PropertyDelegate propertyDelegate) {
            super(inventory, index, x, y);
            this.propertyDelegate = propertyDelegate;
        }

        @Override
        public boolean isEnabled() {
            return propertyDelegate.get(2) != 1;
        }
    }
}
