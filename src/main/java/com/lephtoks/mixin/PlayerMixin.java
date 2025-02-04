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
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.world.explosion.Explosion.getExposure;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDataAccessor {
	private static final double POWER = 10;
	private static final double KNOCKBACK_MODIFIER = 2;

	@Shadow abstract
	PlayerInventory getInventory();
	protected PlayerMixin(EntityType<? extends LivingEntity> type, World level) {
		super(type, level);
	}
	private double heat;
	private double lastHeat = heat;
	private int heatTimer = 0;
	private float cd_ticks;
	private int combo = 0;
	private int combo_unlim = 0;
	@Shadow abstract float getAttackCooldownProgressPerTick();

	@Override
	public double getHeat() {
		return this.heat;
	}

	@Override
	public void setHeat(double value) {
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
		if (getHeat()>10) {
			double intensity = 4000 / (getHeat()+99);
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
	public boolean inGoldRatio(float baseTime, float delta) {
		delta *= 0.5;
		return Math.abs(((float)this.lastAttackedTicks + baseTime) / this.getAttackCooldownProgressPerTick() - 1 - delta) < delta;
	}
	@Inject(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F", shift = At.Shift.AFTER))
	private void setCD(Entity target, CallbackInfo ci) {
		cd_ticks = ((float)this.lastAttackedTicks) / this.getAttackCooldownProgressPerTick();
	}
	@Inject(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.BY, by = -3))
	private void onAttack(Entity target, CallbackInfo ci, @Local(ordinal = 2) boolean bl3, @Local(ordinal = 2) float cd, @Local(ordinal = 3) LocalFloatRef i, @Local(ordinal = 3) boolean bl4) {

		if (target.getWorld() instanceof ServerWorld serverWorld) {
			ItemStack item = this.getInventory().getMainHandStack();
			if (bl4 || bl3) {
				EnchUtils.ifHas(item, TaintedEnchantmentsEffectComponentTypes.GOLD_RATIO_SWING, (effect, level) -> {
					boolean inGoldRatio = cd_ticks < 1 + effect.spread().getValue(level);
					if (inGoldRatio) {
						combo = (int) Math.min(effect.max_combo().getValue(level), combo + 1);
						combo_unlim += 1;
						TaintboundAdvancements.GOLD_RATIO.trigger((ServerPlayerEntity) (Object) this, combo_unlim);
						HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = HashMultimap.create();
						map.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(Identifier.of(TaintboundMod.MOD_ID, "gold_ratio_speed"), effect.acceleration().getValue(level) * combo, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
						this.getAttributes().addTemporaryModifiers(map);

						i.set(i.get() * effect.damage().getValue(level) * Math.max(combo*0.2f, 1));
						serverWorld.spawnParticles(com.lephtoks.particles.ParticleTypes.CORRUPTION_PARTICLE, target.getX(), target.getY() + target.getHeight() * 0.5f, target.getZ(), 50, 0, 0, 0, 0.2f);
					} else if (combo!=0) {

						double w = this.getX() - target.getX();
						double x = this.getEyeY() - target.getY();
						double y = this.getZ() - target.getZ();
						double z = Math.sqrt(w * w + x * x + y * y);
						if (z != 0) {

							w /= z;
							x /= z;
							y /= z;
							double v = Math.sqrt(this.squaredDistanceTo(target.getPos())) / POWER;

							double aa = (1.0 - v) * (double) getExposure(target.getPos(), this) * KNOCKBACK_MODIFIER;
							double ab = aa * (1.0 - this.getAttributeValue(EntityAttributes.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE));

							w *= ab;
							x *= ab;
							y *= ab;
							this.setVelocity(this.getVelocity().add(w, x, y));
						}

						removeAttackSpeedBuff();
					}
				});
			} else if (combo!=0) {
				removeAttackSpeedBuff();
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
	}
	private void removeAttackSpeedBuff() {
		HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = HashMultimap.create();
		map.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(Identifier.of(TaintboundMod.MOD_ID, "gold_ratio_speed"), 0, EntityAttributeModifier.Operation.ADD_VALUE));
		this.getAttributes().removeModifiers(map);
		combo = 0;
		combo_unlim = 0;
	}
	private void knockingImpulse() {

	}
}