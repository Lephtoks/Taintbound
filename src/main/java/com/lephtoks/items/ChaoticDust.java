package com.lephtoks.items;

import com.lephtoks.components.TaintedEnchantmentsDataComponentTypes;
import com.lephtoks.enchantments.TaintedEnchantmentsTag;
import com.lephtoks.particles.ParticleTypes;
import com.lephtoks.utils.ChallengeUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

import java.util.List;

public class ChaoticDust extends Item {

    public ChaoticDust(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        Vec3d vec = Vec3d.of(context.getSide().getVector()).multiply(0.5).add(context.getHitPos());
        if (context.getWorld().isClient()) {
            ParticleUtil.spawnParticles(context.getWorld(), BlockPos.ofFloored(vec.x, vec.y-1, vec.z), ParticleTypes.CHAOTIC_SAND, ConstantIntProvider.create(50), Direction.UP, () -> Vec3d.ZERO, 1.5);
            ParticleUtil.spawnParticles(context.getWorld(), BlockPos.ofFloored(vec.x, vec.y-1, vec.z), ParticleTypes.CHAOTIC_SAND, ConstantIntProvider.create(50), Direction.UP, () -> Vec3d.ZERO, 1.4);
        }
        List<ItemEntity> entities = context.getWorld().getEntitiesByClass(ItemEntity.class, Box.of(vec, 1, 1, 1), (e) -> true);
        stop:
        if (entities.stream().anyMatch((e) -> e.getStack().getItem() == TaintedItems.DARK_GRIMOIRE)) {
            for (ItemEntity itemEntity : entities) {
                ItemStack stack = itemEntity.getStack();
                for (RegistryEntry<Enchantment> entry : stack.getEnchantments().getEnchantments()) {
                    int lvl = stack.getEnchantments().getLevel(entry);
                    if (entry.isIn(TaintedEnchantmentsTag.CAN_GET_CHALLENGE) && lvl > 1) {
                        var builder = ChallengeUtils.getFor(entry);
                        int charge = entry.value().getMaxLevel() - lvl + 1;
                        builder.costAtFirstLevel += (float) Math.pow(builder.costAtFirstLevel * (charge * -0.3f + 0.11f * charge * charge + 1.19f), 0.9);
                        stack.set(TaintedEnchantmentsDataComponentTypes.CHALLENGE, builder.build(true, entry));
                        break stop;
                    }
                }
            }
        }
        context.getStack().setCount(context.getStack().getCount() - 1);
        super.useOnBlock(context);
        return ActionResult.SUCCESS;
    }

}
