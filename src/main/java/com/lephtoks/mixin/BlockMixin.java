package com.lephtoks.mixin;

import com.lephtoks.enchantments.TaintedEnchantmentsEffectComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Block.class)
public abstract class BlockMixin {
	@Inject(at = @At(value = "HEAD"), method = "onBreak")
	private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> callbackInfo) {
		if (player != null && !world.isClient) {
			ItemStack itemStack = player.getInventory().getMainHandStack();
			ItemEnchantmentsComponent enchantmentsComponent = itemStack.getEnchantments();
			for (RegistryEntry<Enchantment> enchantment : enchantmentsComponent.getEnchantments()) {
				enchantment.value().getEffect(TaintedEnchantmentsEffectComponentTypes.BLOCK_BREAK).forEach((effect) -> {
					effect.effect().apply(world.getServer().getWorld(world.getRegistryKey()), enchantmentsComponent.getLevel(enchantment), new EnchantmentEffectContext(itemStack, player.getPreferredEquipmentSlot(itemStack), player), player, pos.toCenterPos());
				});
			}
		}
	}
}