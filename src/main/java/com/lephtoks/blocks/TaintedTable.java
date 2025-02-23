package com.lephtoks.blocks;

import com.lephtoks.blockentities.TaintedBlockEntityTypes;
import com.lephtoks.blockentities.TaintedTableBlockEntity;
import com.lephtoks.components.BrokenEnchantmentAbilityComponent;
import com.lephtoks.components.TaintedEnchantmentsDataComponentTypes;
import com.lephtoks.enchantments.TaintedEnchantments;
import com.lephtoks.recipes.OnePropertiedItemRecipeInput;
import com.lephtoks.recipes.TaintboundRecipes;
import com.lephtoks.recipes.taintedtable.EnchantedRecipeInput;
import com.lephtoks.recipes.taintedtable.TaintedTableRecipe;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Set;

public class TaintedTable extends BlockWithEntity {


    public static final MapCodec<TaintedTable> CODEC = createCodec(TaintedTable::new);
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 12, 16);
    private boolean isCrafting;
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public TaintedTable(Settings settings) {
        super(settings);
    }

    static boolean itemIsBroken(ItemStack itemStack) {
        return itemStack.contains(TaintedEnchantmentsDataComponentTypes.BROKEN_ENCHANTMENT_ABILITY);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof TaintedTableBlockEntity entity)) {
            return ActionResult.PASS;
        }
        boolean isPresent = entity.currentRecipe != null;

        ActionResult actionResult = preUse(entity, state, world, pos, player, hit);

        if (actionResult == ActionResult.SUCCESS) {
            if (this.isCrafting) {
                world.playSound(player, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1, 1);

            } else {
                if (!isPresent && entity.currentRecipe != null) {
                    ParticleUtil.spawnParticlesAround(world, pos.up(), 27, 0, -0.25, false, ParticleTypes.WITCH);
                    world.playSound(player, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1, 1);
                } else if (isPresent && entity.currentRecipe == null) {
                    ParticleUtil.spawnParticlesAround(world, pos.up(), 30, 0, -0.25, false, ParticleTypes.SMOKE);
                    world.playSound(player, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
                }
            }
        }
        this.isCrafting = false;
        return actionResult;
    }
    private ActionResult preUse(TaintedTableBlockEntity entity, BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        ItemStack item = player.getInventory().getMainHandStack();
        if (!item.isEmpty()) {
            if (!entity.enchIsEmpty()) {
                Optional<RegistryEntry<Enchantment>> destabilisation = entity.getEnchantment(TaintedEnchantments.DESTABILISATION);
                if(destabilisation.isPresent()) {
                    OnePropertiedItemRecipeInput input = new OnePropertiedItemRecipeInput(player.getMainHandStack(), entity.enchantments.get(destabilisation.get()));

                    var currentRecipe = world.getRecipeManager().getFirstMatch(TaintboundRecipes.DESTABILISATION_RECIPE, input, world);
                    if (currentRecipe.isPresent()) {
                        if (player.getMainHandStack().getCount() == 1) {
                            player.getInventory().main.set(player.getInventory().selectedSlot, currentRecipe.get().value().craft(input, world.getRegistryManager()));
                        } else {
                            player.getMainHandStack().setCount(player.getMainHandStack().getCount()-1);
                            player.giveItemStack(currentRecipe.get().value().craft(input, world.getRegistryManager()));
                        }
                        entity.enchantments.remove(destabilisation.get());
                        return ActionResult.SUCCESS;
                    }
                }
            }
            ItemEnchantmentsComponent ict;
            boolean isBroken = itemIsBroken(item);
            if (isBroken || item.getItem() == Items.BOOK) {
                if (!entity.enchIsEmpty()) {
                    EnchantedRecipeInput input = new EnchantedRecipeInput(entity.getInventory(), entity.createEnchantmentComponent());
                    var currentRecipe = world.getRecipeManager().getFirstMatch(TaintboundRecipes.TET_RECIPE, input, world);
                    Optional<TaintedTableRecipe> optionalTaintedTableRecipe = currentRecipe.map(RecipeEntry::value);

                    if (optionalTaintedTableRecipe.isPresent() && !entity.enchantments.keySet().containsAll(optionalTaintedTableRecipe.get().getResultEnchant().getEnchantments())) {
                        ItemEnchantmentsComponent iecInput = optionalTaintedTableRecipe.get().getInputEnchantments();
                        var builder = optionalTaintedTableRecipe.get().craftEnchant();
                        for (RegistryEntry<Enchantment> entry: builder.getEnchantments()) {
                            entity.enchantments.put(entry, builder.getLevel(entry));
                        }
                        for (RegistryEntry<Enchantment> entry: iecInput.getEnchantments()) {
                            entity.enchantments.remove(entry);
                        }
                        entity.clear();
                        this.isCrafting = true;
                    }
                    if (item.getItem() == Items.BOOK) {
                        ItemStack item_new = new ItemStack(Items.ENCHANTED_BOOK, 1);
                        EnchantmentHelper.set(item_new, entity.createEnchantmentComponent());
                        if (item.getCount() > 1) {
                            player.giveItemStack(item_new);
                            item.decrement(1);
                        } else {
                            player.getInventory().main.set(player.getInventory().selectedSlot, item_new);
                        }
                    } else {
                        item.remove(TaintedEnchantmentsDataComponentTypes.BROKEN_ENCHANTMENT_ABILITY);
                        EnchantmentHelper.set(item, entity.createEnchantmentComponent());
                    }
                    entity.enchantments.clear();
                    entity.updateRecipe();
                    return ActionResult.SUCCESS;
                }
                else {
                    return ActionResult.PASS;
                }
            }
            else if ((ict = EnchantmentHelper.getEnchantments(item)) != ItemEnchantmentsComponent.DEFAULT) {
                if (entity.enchIsEmpty()) {
                    entity.loadEnchantmentComponent(ict);
                    item.remove(DataComponentTypes.ENCHANTMENTS);
                    if (item.getItem() == Items.ENCHANTED_BOOK) {
                        player.getInventory().main.set(player.getInventory().selectedSlot, new ItemStack(Items.BOOK, 1));
                    } else {
                        item.set(TaintedEnchantmentsDataComponentTypes.BROKEN_ENCHANTMENT_ABILITY, new BrokenEnchantmentAbilityComponent(true));
                    }
                    entity.updateRecipe();
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS;
                }
            } else if (entity.appendStack(item.copyWithCount(1))) {
                world.playSound(player, pos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM,SoundCategory.PLAYERS, 1, 1);
                item.setCount(item.getCount() - 1);
                entity.updateRecipe();
                return ActionResult.SUCCESS;
            }
        } else if (player.isSneaking()) {
            EnchantedRecipeInput input = new EnchantedRecipeInput(entity.getInventory(), entity.createEnchantmentComponent());
            var currentRecipe = world.getRecipeManager().getFirstMatch(TaintboundRecipes.TET_RECIPE, input, world);
            Optional<TaintedTableRecipe> optionalTaintedTableRecipe = currentRecipe.map(RecipeEntry::value);

            if (optionalTaintedTableRecipe.isPresent()) {
                if (entity.enchIsEmpty()) {
                    entity.enchantments.clear();
                }
                var enchs = new java.util.HashSet<>(optionalTaintedTableRecipe.get().getResultEnchant().getEnchantments());
                enchs.retainAll(entity.enchantments.keySet());

                if (optionalTaintedTableRecipe.get().getInputEnchantments().getEnchantments().containsAll(enchs)) {
                    ItemEnchantmentsComponent iecInput = optionalTaintedTableRecipe.get().getInputEnchantments();
                    var builder = optionalTaintedTableRecipe.get().craftEnchant();
                    for (RegistryEntry<Enchantment> entry : iecInput.getEnchantments()) {
                        entity.enchantments.remove(entry);
                    }
                    for (RegistryEntry<Enchantment> entry : builder.getEnchantments()) {
                        entity.enchantments.put(entry, builder.getLevel(entry));
                    }
                    entity.clear();
                    this.simulateCraft(entity);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        } else {
            Optional<ItemStack> itemStack = entity.popStack();
            if (itemStack.isPresent()) {
                entity.updateRecipe();
                player.giveItemStack(itemStack.get());
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? validateTicker(type, TaintedBlockEntityTypes.TAINTED_TABLE, TaintedTableBlockEntity::tick) : null;
    }
    private static Optional<RegistryEntry<Enchantment>> getEnchantment(Set<RegistryEntry<Enchantment>> enchantments, RegistryKey<Enchantment> key) {
        return enchantments.stream().filter((entry) -> entry.matchesKey(key)).findFirst();
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TaintedTableBlockEntity(pos, state);
    }
    private void simulateCraft(TaintedTableBlockEntity entity) {
        entity.updateRecipe();
        this.isCrafting = true;
    }
    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
