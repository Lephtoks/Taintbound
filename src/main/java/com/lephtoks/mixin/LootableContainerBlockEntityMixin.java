package com.lephtoks.mixin;

import com.lephtoks.TaintboundMod;
import com.lephtoks.mixinaccessors.LootBlockAccessor;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin implements LootBlockAccessor {
	@Shadow @Nullable protected RegistryKey<LootTable> lootTable;
	@Unique
	boolean isChanged = false;

	@Override
	public boolean taintedEnchantments$isChanged() {
		return this.isChanged;
	}

	@Override
	public void taintedEnchantments$setIsChanged(boolean changed) {
		this.isChanged = changed;
		TaintboundMod.LOGGER.info("OKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKw");
	}

	@Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At(value = "HEAD"))
	private void removeStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
		if (this.lootTable == null) taintedEnchantments$setIsChanged(true);
	}

	@Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At(value = "HEAD"))
	private void removeStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
		if (this.lootTable == null) taintedEnchantments$setIsChanged(true);
	}

	@Inject(method = "setStack", at = @At(value = "HEAD"))
	private void setStack(int slot, ItemStack stack, CallbackInfo ci) {
		if (this.lootTable == null) taintedEnchantments$setIsChanged(true);
	}
}