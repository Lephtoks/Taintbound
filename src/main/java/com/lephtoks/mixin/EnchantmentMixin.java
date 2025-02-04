package com.lephtoks.mixin;

import com.lephtoks.TaintboundMod;
import com.lephtoks.enchantments.TaintedEnchantmentsTag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Enchantment.class)
public class EnchantmentMixin {

	@Inject(at = @At(value = "RETURN"), method = "getName")
	private static void init(RegistryEntry<Enchantment> enchantment, int level, CallbackInfoReturnable<MutableText> callbackInfo) {
		if (enchantment.value().getMaxLevel() != 1 && enchantment.isIn(TaintedEnchantmentsTag.TAINTED_ENCHANTMENTS_SET)) {
			var siblings = callbackInfo.getReturnValue().getSiblings();
			siblings.removeLast();
			siblings.addLast(Text.of(String.valueOf(TaintboundMod.CHARGES[level - 1])));
		}
	}
}