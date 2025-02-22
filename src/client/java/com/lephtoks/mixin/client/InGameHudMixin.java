package com.lephtoks.mixin.client;

import com.lephtoks.enchantments.TaintedEnchantmentsEffectComponentTypes;
import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import com.lephtoks.utils.EnchUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.lephtoks.TaintboundMod.MOD_ID;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public abstract class InGameHudMixin {
	@Unique
	private DrawContext context;
	@Final
	@Shadow
    private MinecraftClient client;
	@Final
	@Shadow private static Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE;
	@Unique
	private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE_CORRUPTED = Identifier.of(MOD_ID, "hud/crosshair_attack_indicator_full_corrupted");

	@Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE:Lnet/minecraft/util/Identifier;", opcode = Opcodes.GETSTATIC))
	private Identifier injected() {
		if (this.client.player != null &&
				EnchUtils.ifHas(this.client.player.getInventory().getMainHandStack(), TaintedEnchantmentsEffectComponentTypes.GOLD_RATIO_SWING, (effect, level) -> {
					return ((PlayerDataAccessor) this.client.player).taintedEnchantments$inGoldRatio(0, effect.spread().getValue(level));
				})
		) {
			return CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE_CORRUPTED;
		}
		return CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE;
	}
}