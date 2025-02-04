package com.lephtoks.blockentities;

import com.lephtoks.blocks.AbstractHollowCore;
import com.lephtoks.recipes.OnePropertiedItemRecipeInput;
import com.lephtoks.recipes.TaintboundRecipes;
import com.lephtoks.recipes.hollow_core.HollowCoreRecipe;
import com.lephtoks.screen.HollowCoreScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.math3.analysis.integration.MidPointIntegrator;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.jetbrains.annotations.Nullable;

public class HollowCoreBlockEntity extends BlockEntity implements SingleStackInventory, NamedScreenHandlerFactory {
    public static final float MAX_ENERGY = 144000*5;

    private final static MidPointIntegrator integrator = new MidPointIntegrator(1, 15);
    private short craft_tick = 0;
    @Nullable public HollowCoreRecipe currentRecipe;

    public static double energyGeneration(double v) {
        return (Math.abs(Math.abs(v-6000) % 24000 - 12000) - 6000) * 0.002;
    }
    public int ticks = 0;
    ItemStack inventory = ItemStack.EMPTY;
    float energy = 0f;
    PropertyDelegate propertyDelegate;
    private int isCrafting = 0;

    public HollowCoreBlockEntity(BlockEntityType<HollowCoreBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);

        this.propertyDelegate = new ArrayPropertyDelegate(4);
    }
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

        NbtCompound enchantment = nbt.getCompound("item");

        inventory = ItemStack.CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), enchantment).result().orElse(ItemStack.EMPTY);

        energy = nbt.getFloat("energy");

        if (hasWorld()) updateRecipes();
    }

    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (!inventory.isEmpty()) {
            nbt.put("item", inventory.encode(registryLookup));
        }
        nbt.putFloat("energy", energy);
    }

    public static void tick(World world, BlockPos pos, BlockState state, HollowCoreBlockEntity blockEntity) {
        if (world.isClient) {
            ++blockEntity.ticks;
        }
        else {
            if (blockEntity.viewers > 0) {
                blockEntity.propertyDelegate.set(0, (int) (blockEntity.energy * 10000 / MAX_ENERGY));
                blockEntity.propertyDelegate.set(1, (int) ((blockEntity.energy + predictEnergyDif((int) world.getTimeOfDay())) * 10000 / MAX_ENERGY));
            }
            if (((AbstractHollowCore)state.getBlock()).isWorking(world, state)) {
                blockEntity.energy = Math.clamp(blockEntity.energy + (float) energyGeneration(world.getTimeOfDay()) * 2, -MAX_ENERGY, MAX_ENERGY);
            }
            if (blockEntity.currentRecipe != null) {
                ItemStack result = blockEntity.currentRecipe.getResult(world.getRegistryManager());
                int count = blockEntity.inventory.getCount();
                int resultCount = result.getCount() * count;
                int energyCost = blockEntity.currentRecipe.getCost() * count;
                if (resultCount <= result.getMaxCount() && AbstractHollowCore.energyQMoreThan(blockEntity.energy, energyCost)) {
                    blockEntity.isCrafting = 1;
                    blockEntity.craft_tick += 1;
                    int creationTime = count * 100;
                    if (blockEntity.craft_tick > creationTime) {
                        blockEntity.energy -= energyCost;
                        blockEntity.isCrafting = 0;
                        blockEntity.craft_tick = 0;
                        blockEntity.setStack(result.copyWithCount(resultCount));
                    }
                    blockEntity.propertyDelegate.set(3, blockEntity.craft_tick * 10000 / (creationTime));
                }
            } else {
                blockEntity.isCrafting = 0;
                blockEntity.craft_tick = 0;
            }
            blockEntity.propertyDelegate.set(2, blockEntity.isCrafting);
        }
    }
    int viewers = 0;
    @Override
    public void onOpen(PlayerEntity player) {
        SingleStackInventory.super.onOpen(player);
        viewers++;
    }

    @Override
    public void onClose(PlayerEntity player) {
        SingleStackInventory.super.onClose(player);
        viewers--;
    }

    @Override
    public ItemStack getStack() {
        return inventory;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.inventory = stack;
        updateRecipes();
        markDirty();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        updateRecipes();
    }

    public void updateRecipes() {
        this.currentRecipe = this.world.getRecipeManager().getFirstMatch(TaintboundRecipes.HC_RECIPE, new OnePropertiedItemRecipeInput(this.inventory, (int) this.energy), world).map(RecipeEntry::value).orElse(null);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
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
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() != null && !this.getWorld().isClient() && this.world instanceof ServerWorld w) {
            w.getChunkManager().markForUpdate(getPos());
        }
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HollowCoreScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    public static float predictEnergyDif(int t) {
//        t = t % 24000;
//        float a1 = (float) energyGeneration(t);
//        int td = t * 60 * 20 * 5;
//        float an = (float) energyGeneration(td);
//        if ((a1 < an == (Math.signum(a1) == Math.signum(an))) && a1 != an) {
//            return (a1+an)*0.5f * 600*2*5;
//        } else {
//            int tp = a1 > 0 ? 6000 : 18000;
//            float ap = (float) energyGeneration(tp);
//            return (a1+ap)*0.5f*(tp-t) + (ap+an)*0.5f*(600*2*5-tp+t);
//        }
        try {
            return ((float) integrator.integrate(1000, HollowCoreBlockEntity::energyGeneration, t, t + 20 * 60 * 5)*2 + (float) energyGeneration(t) + (float) energyGeneration(t + 20 * 60 * 5))*0.5f;
        }
        catch (TooManyEvaluationsException e) {
            return 0;
        }
    }
}
