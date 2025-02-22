package com.lephtoks.mixin;

import com.google.common.collect.HashMultimap;
import com.lephtoks.TaintboundMod;
import com.lephtoks.advancements.TaintboundAdvancements;
import com.lephtoks.enchantments.TaintedEnchantmentsEffectComponentTypes;
import com.lephtoks.mixinaccessors.DamageSourcesDataAccessor;
import com.lephtoks.mixinaccessors.PlayerDataAccessor;
import com.lephtoks.mixinaccessors.StatusEffectInstanceAccessor;
import com.lephtoks.utils.EnchUtils;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDataAccessor {
	@Unique
	private static final double POWER = 1.25;

	@Shadow
    public abstract PlayerInventory getInventory();
	protected PlayerMixin(EntityType<? extends LivingEntity> type, World level) {
		super(type, level);
	}
	@Unique
	private double heat;
	@Unique
	private double lastHeat = heat;
	@Unique
	private int heatTimer = 0;
	@Unique
	private float cd_ticks;
	@Unique
	private int combo = 0;
	@Unique
	private int combo_unlim = 0;
	@Shadow
    public abstract float getAttackCooldownProgressPerTick();

	@Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

	@Shadow @Final private static Logger LOGGER;

	@Override
	public double taintedEnchantments$getHeat() {
		return this.heat;
	}

	@Override
	public void taintedEnchantments$setHeat(double value) {
		this.heat = value;
		if ((PlayerEntity) (Object) this instanceof ServerPlayerEntity player) {
			TaintboundAdvancements.HEAT_VALUE.trigger(player);
		}
	}

	@Inject(at = @At(value = "HEAD"), method = "writeCustomDataToNbt")
	private void write(NbtCompound main, CallbackInfo callbackInfo) {
		NbtCompound nbt = new NbtCompound();
		nbt.putDouble("heat", this.heat);
		main.put(TaintboundMod.MOD_ID, nbt);
	}
	@Inject(at = @At(value = "HEAD"), method = "readCustomDataFromNbt")
	private void read(NbtCompound main, CallbackInfo callbackInfo) {
		NbtCompound nbt = main.getCompound(TaintboundMod.MOD_ID);
		this.heat = nbt.getDouble("heat");
	}
	@Inject(at = @At(value = "HEAD"), method = "tick")
	private void ticking(CallbackInfo callbackInfo) {
		if (this.getWorld().isClient) return;
		if (taintedEnchantments$getHeat()>10) {
			double intensity = 4000 / (taintedEnchantments$getHeat()+99);
			if (heatTimer > intensity) {
				damage(((DamageSourcesDataAccessor)getWorld().getDamageSources()).heatDamage(), 0.5f);
				heatTimer = 0;
				StatusEffectInstance statusEffectInstance = getActiveStatusEffects().get(StatusEffects.FIRE_RESISTANCE);
				if (this.removeStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
					((StatusEffectInstanceAccessor) statusEffectInstance).reduce(30);
					this.addStatusEffect(statusEffectInstance);
				}
				if (heat == lastHeat) {
					heat -= Math.sqrt(heat);
				}
				heat -= 2;
				lastHeat = heat;
			}
			heatTimer++;
			HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = HashMultimap.create();
			map.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(Identifier.of(TaintboundMod.MOD_ID, "heat_speed"), Math.min((heat - 10)*0.00025, 0.25), EntityAttributeModifier.Operation.ADD_VALUE));
			this.getAttributes().removeModifiers(map);
			this.getAttributes().addTemporaryModifiers(map);
		} else {
			HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = HashMultimap.create();
			map.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(Identifier.of(TaintboundMod.MOD_ID, "heat_speed"), 0, EntityAttributeModifier.Operation.ADD_VALUE));
			this.getAttributes().removeModifiers(map);
		}
	}
	public boolean taintedEnchantments$inGoldRatio(float baseTime, float delta) {
		delta *= 0.5F;
		return Math.abs(((float)this.lastAttackedTicks + baseTime) / this.getAttackCooldownProgressPerTick() - 1 - delta) < delta;
	}
	@Inject(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F", shift = At.Shift.AFTER))
	private void setCD(Entity target, CallbackInfo ci) {
		cd_ticks = ((float)this.lastAttackedTicks) / this.getAttackCooldownProgressPerTick();
	}
	@Inject(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.BY, by = -3))
	private void onAttack(Entity target, CallbackInfo ci, @Local(ordinal = 2) boolean bl3, @Local(ordinal = 2) float cd, @Local(ordinal = 3) LocalFloatRef i, @Local(ordinal = 3) boolean bl4) {
		ItemStack item = this.getInventory().getMainHandStack();
		if (bl4 || bl3) {
			EnchUtils.ifHas(item, TaintedEnchantmentsEffectComponentTypes.GOLD_RATIO_SWING, (effect, level) -> {
				boolean inGoldRatio = cd_ticks < 1 + effect.spread().getValue(level);
				if (inGoldRatio) {
					combo = (int) Math.min(effect.max_combo().getValue(level), combo + 1);
					combo_unlim += 1;
					if (target.getWorld() instanceof ServerWorld serverWorld) {
                        //noinspection DataFlowIssue
                        TaintboundAdvancements.GOLD_RATIO.trigger((ServerPlayerEntity) (Object) this, combo_unlim);
						HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = HashMultimap.create();
						float delta = random.nextFloat()*10f;
						map.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(Identifier.of(TaintboundMod.MOD_ID, "gold_ratio_speed"), effect.acceleration().getValue(level) * Math.max(combo - delta, 0), EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
						this.getAttributes().addTemporaryModifiers(map);

						i.set(i.get() * effect.damage().getValue(level) * Math.max(combo*0.2f, 1));
						serverWorld.spawnParticles(com.lephtoks.particles.ParticleTypes.CORRUPTION_PARTICLE, target.getX(), target.getY() + target.getHeight() * 0.5f, target.getZ(), 50, 0, 0, 0, 0.2f);
					}
				} else if (combo!=0) {
					knock(target);
					taintedEnchantments$removeAttackSpeedBuff();
				}
			});
		} else if (combo!=0) {
			knock(target);
			taintedEnchantments$removeAttackSpeedBuff();
		}
		EnchUtils.ifHas(item, TaintedEnchantmentsEffectComponentTypes.RANDOM_DAMAGE, (effect, level) -> {
			AtomicInteger sum = new AtomicInteger();
			List<Integer> weights = effect.weights().stream().map((entry) -> {
				int v = (int) entry.getValue(level);
				sum.addAndGet(v);
				return v;
			}).toList();
			Iterator<Integer> iterator = weights.iterator();
			int sum2 = 0;
			int t = random.nextInt(sum.get());
			int ii = 0;
			while (iterator.hasNext()) {
				int n = iterator.next();
				sum2 += n;
				if (t < sum2) {
					i.set(i.get() * effect.modifiers().get(ii).getValue(level));
					return;
				}
				ii++;
			}
		});
	}
	@Unique
	private void knock(Entity target) {
		if (this.getWorld().isClient()) {
			this.playSound(SoundEvents.ITEM_MACE_SMASH_GROUND);
		}
		Vec3d dir = this.getPos().subtract(target.getPos());
		Vec3d multiply = dir.normalize().multiply(POWER / (Math.max(this.squaredDistanceTo(target.getPos()), 0.8)));
		this.addVelocityInternal(multiply.add(0, 1, 0));
	}
	@Unique
	public void taintedEnchantments$removeAttackSpeedBuff() {
		LOGGER.info(String.valueOf(combo));
		HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = HashMultimap.create();
		map.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(Identifier.of(TaintboundMod.MOD_ID, "gold_ratio_speed"), 0, EntityAttributeModifier.Operation.ADD_VALUE));
		this.getAttributes().removeModifiers(map);
		combo = 0;
		combo_unlim = 0;
	}
}