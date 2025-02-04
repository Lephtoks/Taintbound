package com.lephtoks.enchantments;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public record BreakAdjacentEnchantmentEffect(EnchantmentLevelBasedValue percent) implements EnchantmentEntityEffect {
    public static final MapCodec<BreakAdjacentEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("percent").forGetter((breakAdjacentEnchantmentEffect) -> {
            return breakAdjacentEnchantmentEffect.percent;
        })).apply(instance, BreakAdjacentEnchantmentEffect::new);
    });

    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        BlockPos targetPos = BlockPos.ofFloored(pos);
        Block targetBlock = world.getBlockState(targetPos).getBlock();
        int remain = (int) percent.getValue(level);

        Set<BlockPos> current = new HashSet<>();
        current.add(targetPos);
        Set<BlockPos> addition = new HashSet<>();

        boolean lowLevelCheck = user.getFacing() != Direction.DOWN;
        int lowLevel = targetPos.getY() - 1;

        while (!current.isEmpty()) {
            Consumer<BlockPos> addIfEqual = (block) -> {
                if (world.getBlockState(block).getBlock() == targetBlock && (!lowLevelCheck || block.getY() >= lowLevel)) addition.add(block);
            };
            for (BlockPos blockPos : current) {
                addIfEqual.accept(blockPos.up());
                addIfEqual.accept(blockPos.down());
                addIfEqual.accept(blockPos.east());
                addIfEqual.accept(blockPos.west());
                addIfEqual.accept(blockPos.south());
                addIfEqual.accept(blockPos.north());

                if (blockPos != targetPos) {
                    world.breakBlock(blockPos, !((PlayerEntity) user).isInCreativeMode(), user);
                    remain--;
                    if (remain <= 0) return;
                }
            }
            current.clear();
            current.addAll(addition);
            addition.clear();
        }
    }

    public MapCodec<BreakAdjacentEnchantmentEffect> getCodec() {
        return CODEC;
    }

    public EnchantmentLevelBasedValue percent() {
        return this.percent;
    }
}