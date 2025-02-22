package com.lephtoks.mixin.client;

import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

	@Shadow @Final private MinecraftClient client;

	@Inject(method = "syncSelectedSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
	private void scroll(CallbackInfo ci) {
		PlayerDataAccessor player = (PlayerDataAccessor) this.client.player;
		if (player != null) {
			player.taintedEnchantments$removeAttackSpeedBuff();
		}
	}
}