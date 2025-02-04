package com.lephtoks.mixin;

import com.lephtoks.damagetypes.TaintedEnchantmentsDamageTypes;
import com.lephtoks.mixinaccessors.DamageSourcesDataAccessor;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DamageSources.class)
public abstract class DamageSourcesMixin implements DamageSourcesDataAccessor {
	private DamageSource heat;

	@Inject(at = @At(value = "TAIL"), method = "<init>")
	private void init(DynamicRegistryManager dynamicRegistryManager, CallbackInfo callbackInfo) {
		this.heat = this.create(TaintedEnchantmentsDamageTypes.HEAT_DAMAGE);
	}
	@Shadow
	public abstract  <T> DamageSource create(RegistryKey<T> key);

	@Override
	public DamageSource heatDamage() {
		return heat;
	}
}