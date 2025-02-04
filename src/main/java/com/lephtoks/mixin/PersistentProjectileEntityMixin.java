package com.lephtoks.mixin;

import com.lephtoks.TaintboundMod;
import com.lephtoks.mixinaccessors.PersistenProjectileEntityAccessor;
import com.lephtoks.utils.ModEffectUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public abstract class PersistentProjectileEntityMixin extends PersistentProjectileEntity implements PersistenProjectileEntityAccessor {
    private static final TrackedData<Boolean> corrupted = DataTracker.registerData(PersistentProjectileEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    public PersistentProjectileEntityMixin(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isCorrupted() {
        return this.dataTracker.get(corrupted);
    }

    @Override
    public void setCorruption(boolean corruption) {
        this.dataTracker.set(corrupted, corruption);
    }


    @Override
    public void writeCustomDataToNbt(NbtCompound main) {
        super.writeCustomDataToNbt(main);
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("corrupted", isCorrupted());
        main.put(TaintboundMod.MOD_ID, nbt);
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound main) {
        super.readCustomDataFromNbt(main);
        NbtCompound nbt = main.getCompound(TaintboundMod.MOD_ID);
        setCorruption(nbt.getBoolean("corrupted"));
    }
    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        if (isCorrupted() && this.getOwner() instanceof LivingEntity owner && entityHitResult.getEntity() instanceof LivingEntity target && target.canTakeDamage()) {

            if (owner.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(com.lephtoks.particles.ParticleTypes.CORRUPTION_PARTICLE, target.getX(), target.getY() + target.getHeight() * 0.5f, target.getZ(), 50, 0, 0, 0, 0.2f);
            }

            for (StatusEffectInstance instance : owner.getStatusEffects().stream().toList()) {
                var type = ModEffectUtils.getReversed(instance.getEffectType());
                if (type == null) break;
                if (type.value().isInstant()) {
                    target.addStatusEffect(new StatusEffectInstance(type, 1, instance.getAmplifier()));
                } else {
                    target.addStatusEffect(new StatusEffectInstance(type, 100, instance.getAmplifier()));
                }
            }
        }
        super.onEntityHit(entityHitResult);
    }

    @Inject(at = @At(value = "HEAD"), method = "initDataTracker")
    public void initDataTracker(DataTracker.Builder builder, CallbackInfo callbackInfo) {
        builder.add(corrupted, false);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
    }

    @Inject(at = @At(value = "HEAD"), method = "tick")
    private void onTick(CallbackInfo callbackInfo) {
        if (this.isCorrupted() && !this.inGround) {
            this.getWorld().addParticle(ParticleTypes.WITCH, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, -1.0, 0.0);
        }
    }
//    @Inject(at = @At(value = "HEAD"), method = "onBlockHit")
//    private void blockHit(BlockHitResult blockHitResult, CallbackInfo callbackInfo) {
//        ParticleUtil.spawnParticlesAround(this.getWorld(), this.getBlockPos(), 20, ParticleTypes.END_ROD);
//    }
}
