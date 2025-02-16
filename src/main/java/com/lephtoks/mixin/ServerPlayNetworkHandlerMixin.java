package com.lephtoks.mixin;

import com.lephtoks.TaintboundMod;
import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
	@Shadow public ServerPlayerEntity player;

	@Inject(method = "onUpdateSelectedSlot", at = @At("HEAD"))
	private void scroll(UpdateSelectedSlotC2SPacket packet, CallbackInfo ci) {
		TaintboundMod.LOGGER.info("2222");
		((PlayerDataAccessor) this.player).taintedEnchantments$removeAttackSpeedBuff();
	}
}