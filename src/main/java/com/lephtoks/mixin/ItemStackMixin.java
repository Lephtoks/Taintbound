package com.lephtoks.mixin;

import com.lephtoks.items.TaintedEnchantmentsItemTags;
import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();
    @Inject(at = @At(value = "HEAD"), method = "finishUsing")
    private void finish(World world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof PlayerEntity player && Registries.ITEM.getEntry(this.getItem()).isIn(TaintedEnchantmentsItemTags.HEAT_REDUCE)) {
            ((PlayerDataAccessor) player).addHeat(Math.clamp(((PlayerDataAccessor) player).taintedEnchantments$getHeat() * -0.25f, -200, -20));
        }
    }
}