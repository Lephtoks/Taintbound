package com.lephtoks.mixin;

import com.lephtoks.mixinaccessors.StatusEffectInstanceAccessor;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements StatusEffectInstanceAccessor {
	@Shadow private int duration;
	@Shadow abstract int mapDuration(Int2IntFunction runnable);

	public void reduce(int value) {
		this.duration = this.mapDuration((i) -> i-value);
	}
}