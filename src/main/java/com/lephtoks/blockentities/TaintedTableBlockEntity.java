package com.lephtoks.blockentities;

import com.lephtoks.TaintboundMod;
import com.lephtoks.network.UpdateTaintedTableS2CPacket;
import com.lephtoks.recipes.TaintboundRecipes;
import com.lephtoks.recipes.taintedtable.EnchantedRecipeInput;
import com.lephtoks.recipes.taintedtable.TaintedTableRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

public class TaintedTableBlockEntity extends BlockEntity implements Inventory {
    public int ticks = 0;
    public static int SIZE = 8;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
    public Recipe<?> currentRecipe;
    public final HashMap<RegistryEntry<Enchantment>, Integer> enchantments = new HashMap<>();
    public static final UnboundedMapCodec<RegistryEntry<Enchantment>, Integer> ENCHANTMENT_CODEC = Codec.unboundedMap(Enchantment.ENTRY_CODEC, Codec.INT);
    public boolean enchIsEmpty() {
        return enchantments.isEmpty();
    }

    public TaintedTableBlockEntity(BlockPos pos, BlockState state) {
        super(TaintedBlockEntityTypes.TAINTED_TABLE, pos, state);
    }
    public Optional<RegistryEntry<Enchantment>> getEnchantment(RegistryKey<Enchantment> key) {
        return this.enchantments.keySet().stream().filter((entry) -> entry.matchesKey(key)).findFirst();
    }
    public void updateRecipe() {
        EnchantedRecipeInput input = new EnchantedRecipeInput(this.getInventory(), this.createEnchantmentComponent());
        var currentRecipe = world.getRecipeManager().getFirstMatch(TaintboundRecipes.TET_RECIPE, input, world);
        Optional<TaintedTableRecipe> optionalTaintedTableRecipe = currentRecipe.map(RecipeEntry::value);
        sendUpdatePacket(world, pos);
        this.currentRecipe = optionalTaintedTableRecipe.orElse(null);
    }
    private void sendUpdatePacket(World world, BlockPos pos) {
        if (world.isClient) return;
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt, world.getRegistryManager());
        UpdateTaintedTableS2CPacket packet = new UpdateTaintedTableS2CPacket(nbt, pos);
        world.getPlayers().forEach((p) -> ServerPlayNetworking.send(((ServerPlayerEntity) p), packet));

    }
    public ItemEnchantmentsComponent createEnchantmentComponent() {
        ItemEnchantmentsComponent.Builder result = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        this.enchantments.forEach(result::add);
        return result.build();
    }
    public void loadEnchantmentComponent(@Nullable ItemEnchantmentsComponent component) {
        this.enchantments.clear();
        if (component!=null) {
            component.getEnchantments().forEach((entry -> {
                this.enchantments.put(entry, component.getLevel(entry));
            }));
        }
    }
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, this.inventory, registryLookup);

        NbtCompound enchantment = nbt.getCompound("Enchantments");

        enchantments.clear();
        var t =ENCHANTMENT_CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), enchantment).result(
//                (error) -> {
//            TaintedEnchantmentsMod.LOGGER.error("Tried to load invalid enchantments: '{}'", error);
//        }
        ).orElse(new HashMap<>());
        enchantments.putAll(t);
        if (hasWorld()) updateRecipe();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        updateRecipe();
    }

    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, this.inventory, registryLookup);

        nbt.put("Enchantments", ENCHANTMENT_CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), enchantments).getOrThrow());
    }


    public static void tick(World world, BlockPos pos, BlockState state, TaintedTableBlockEntity blockEntity) {
        ++blockEntity.ticks;
    }

    @Override
    public void markDirty() {
        TaintboundMod.LOGGER.info("Marked");
        super.markDirty();
        if (this.getWorld() instanceof ServerWorld) {
            ((ServerWorld) world).getChunkManager().markForUpdate(getPos());
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.getInventory().iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = var1.next();
        } while(itemStack.isEmpty());

        return false;
    }
    public boolean appendStack(ItemStack item) {
        Optional<Integer> slot = getLastEmptyIndex();
        if (slot.isEmpty()) {
            return false;
        }
        this.setStack(slot.get(), item);
        return true;
    }
    public Optional<ItemStack> popStack() {
        Optional<Integer> slot = getLastNotEmptyIndex();
        return slot.map(this::removeStack);
    }

    public Optional<Integer> getLastEmptyIndex() {
        int i = 0;
        for (ItemStack item: getInventory()) {
            if (item.isEmpty()) break;
            i++;
        }
        return i < size() ? Optional.of(i) : Optional.empty();
    }
    public Optional<Integer> getLastNotEmptyIndex() {
        int i = -1;
        for (ItemStack item: getInventory()) {
            if (item.isEmpty()) break;
            i++;
        }
        return i != -1 ? Optional.of(i) : Optional.empty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.getInventory().get(slot);
    }
    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.getInventory(), slot, amount);
        this.markDirty();

        return itemStack;
    }
    @Override
    public ItemStack removeStack(int slot) {
        ItemStack itemStack = Inventories.removeStack(this.getInventory(), slot);
        this.markDirty();
        return itemStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.getInventory().set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        this.getInventory().clear();
        this.markDirty();
    }



    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }
}
