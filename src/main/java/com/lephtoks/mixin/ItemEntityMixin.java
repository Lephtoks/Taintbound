package com.lephtoks.mixin;

import com.lephtoks.challenges.ItemChallengeType;
import com.lephtoks.utils.ChallengeUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
	public ItemEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	@Shadow
	UUID owner;
	@Shadow
	int pickupDelay;
	@Shadow
	abstract ItemStack getStack();
	@Shadow
	abstract void setStack(ItemStack itemStack);

	@Shadow private int itemAge;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE), method = "onPlayerCollision", cancellable = true)
	private void onPlayerCollision(PlayerEntity player, CallbackInfo callbackInfo) {
		if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid()))) {
			ChallengeUtils.addForAllComponents(
				player,
				ItemChallengeType.class,
				this::getStack,

				(stack) -> {
					float remain = stack.getCount();
					player.sendPickup(this, (int) remain);
					return remain;
				},

				(item, remain) -> {
					if (remain<=0) {
						this.discard();
						item.setCount(1);
						callbackInfo.cancel();
					} else {
						item.setCount(remain.intValue());
					}
				}
			);
		}
	}
}