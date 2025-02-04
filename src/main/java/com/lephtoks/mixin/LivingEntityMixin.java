package com.lephtoks.mixin;

import com.lephtoks.challenges.EntityKillChallengeType;
import com.lephtoks.mixinaccessors.LivingEntityAccessor;
import com.lephtoks.utils.ChallengeUtils;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, LivingEntityAccessor {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract void tickStatusEffects();

    @Shadow protected abstract void drop(ServerWorld world, DamageSource damageSource);

    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;drop(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)V", opcode = Opcodes.INVOKEVIRTUAL))
    private void regChallenge(LivingEntity instance, ServerWorld world, DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof PlayerEntity player) {
            if (!ChallengeUtils.addForAllComponents(
                    player,
                    EntityKillChallengeType.class,
                    () -> instance,

                    (entity) -> 1F
            )) {
                drop(world, damageSource);
            };
        }
    }
}
