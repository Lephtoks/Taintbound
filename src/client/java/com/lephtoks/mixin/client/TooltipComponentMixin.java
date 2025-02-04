package com.lephtoks.mixin.client;

import com.lephtoks.TaintboundMod;
import com.lephtoks.client.gui.CustomTooltips;
import com.lephtoks.tooltip.TooltipText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;


@Mixin(TooltipComponent.class)
@Environment(EnvType.CLIENT)
public interface TooltipComponentMixin {
    @Inject(at = @At(value = "HEAD"), method = "of(Lnet/minecraft/text/OrderedText;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", cancellable = true)
    private static void of(OrderedText text, CallbackInfoReturnable<TooltipComponent> cir) {
        if (text instanceof TooltipText(
                Identifier tooltipIdentifier,
                TooltipAppender tooltipAppender
        )) {
            try {
                cir.setReturnValue(CustomTooltips.get(tooltipIdentifier).getConstructor(tooltipAppender.getClass()).newInstance(tooltipAppender));
                cir.cancel();
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                TaintboundMod.LOGGER.error("Impossible to get custom tooltip");
            }
        }
    }
}