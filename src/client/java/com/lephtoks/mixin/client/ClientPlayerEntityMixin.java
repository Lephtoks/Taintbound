package com.lephtoks.mixin.client;

import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	private int heatCD = 0;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V"), method = "tick")
	private void t(CallbackInfo info) {
		if (heatCD >= 3500 / (((PlayerDataAccessor) this).taintedEnchantments$getHeat() + 99)) {
			ParticleUtil.spawnParticlesAround(getWorld(), getBlockPos(), (int) Math.ceil(((PlayerDataAccessor) this).taintedEnchantments$getHeat() * 0.00066f), ParticleTypes.LAVA);
		}
		heatCD++;
	}
}