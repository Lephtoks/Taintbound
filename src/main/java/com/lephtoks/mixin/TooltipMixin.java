package com.lephtoks.mixin;

import com.lephtoks.components.TaintedEnchantmentsDataComponentTypes;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;



@Mixin(ItemStack.class)
public abstract class TooltipMixin implements ComponentHolder {

	@Inject(at = @At(value = "RETURN"), method = "getTooltip")
	private void init(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info) {
		List<Text> list = info.getReturnValue();
		this.appendTooltip(TaintedEnchantmentsDataComponentTypes.BROKEN_ENCHANTMENT_ABILITY, context, (item) -> {list.add(1, item);}, type);
		this.appendTooltip(TaintedEnchantmentsDataComponentTypes.CHALLENGE, context, (text) -> list.add(1, text), type);
	}
	@Shadow
	private <T extends TooltipAppender> void appendTooltip(ComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {}
}