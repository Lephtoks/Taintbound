package com.lephtoks.recipes.taintedtable;

import com.lephtoks.TaintboundMod;
import com.lephtoks.recipes.NBTIngredient;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractTaintedTableRecipe implements Recipe<EnchantedRecipeInput> {

    protected final RecipeType<?> type;
    protected final List<NBTIngredient> ingredients;
    protected final ItemEnchantmentsComponent result;
    protected final ItemEnchantmentsComponent enchantments;
    protected final RecipeSerializer<?> serializer;
    protected final String group;

    public AbstractTaintedTableRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, String group,
                                      ItemEnchantmentsComponent enchantments,
                                      ItemEnchantmentsComponent result, List<NBTIngredient> ingredients) {
        this.type = type;
        this.serializer = serializer;
        this.group = group;

        this.enchantments = enchantments;
        this.ingredients = ingredients;
        this.result = result;
    }

    public static Identifier TYPE_ID = Identifier.of(TaintboundMod.MOD_ID, "tainted_table");

    @Override
    public boolean matches(EnchantedRecipeInput input, World world) {

        List<NBTIngredient> missingIngredients = new ArrayList<>(this.ingredients);
        for (int i = 0; i < input.inventory.size(); i++) {
            ItemStack item = input.getStackInSlot(i);
            if (item.isEmpty()) {break;}

            int stackIndex = -1;

            for (int j = 0; j < missingIngredients.size(); j++) {
                NBTIngredient ingredient = missingIngredients.get(j);
                if (ingredient.test(item)) {
                    stackIndex = j;
                    break;
                }
            }

            if (stackIndex != -1) {
                missingIngredients.remove(stackIndex);
            } else {
                return false;
            }
        }
        if (!missingIngredients.isEmpty()) return false;


        if ((input.enchantments == null || input.enchantments.isEmpty()) && this.enchantments.isEmpty()) return true;
        else if (input.enchantments != null) {
            return input.enchantments.getEnchantments().containsAll(this.enchantments.getEnchantments());
        }
        return false;
    }
    @Override
    public RecipeType<?> getType() {
        return Registries.RECIPE_TYPE.get(TYPE_ID);
    }

    @Override
    public ItemStack craft(EnchantedRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return ItemStack.EMPTY;
    }
    public ItemEnchantmentsComponent.Builder craftEnchant() {
        return new ItemEnchantmentsComponent.Builder(this.result);
    }
    public ItemEnchantmentsComponent getInputEnchantments() {
        return this.enchantments;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }
    public ItemEnchantmentsComponent getResultEnchant() {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }
    @Deprecated
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        this.ingredients.forEach(((n) -> defaultedList.add(n.intoIngredient())));
        return defaultedList;
    }
    public DefaultedList<NBTIngredient> getAdvIngredients() {
        DefaultedList<NBTIngredient> defaultedList = DefaultedList.of();
        defaultedList.addAll(this.ingredients);
        return defaultedList;
    }


    public interface RecipeFactory<T extends AbstractTaintedTableRecipe> {
        T create(String group, ItemEnchantmentsComponent enchantments, ItemEnchantmentsComponent result, List<NBTIngredient> ingredients);
    }

    public static class Serializer<T extends AbstractTaintedTableRecipe> implements RecipeSerializer<T> {
        final AbstractTaintedTableRecipe.RecipeFactory<T> recipeFactory;
        private final MapCodec<T> codec;
        private final PacketCodec<RegistryByteBuf, T> packetCodec;

        public Serializer(AbstractTaintedTableRecipe.RecipeFactory<T> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.codec = RecordCodecBuilder.mapCodec((instance) -> {
                Products.P4<RecordCodecBuilder.Mu<T>, String, ItemEnchantmentsComponent, ItemEnchantmentsComponent, List<NBTIngredient>> var10000 = instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter((recipe) -> recipe.group),
                        ItemEnchantmentsComponent.CODEC.fieldOf("enchantments").forGetter((recipe) -> recipe.enchantments),
                        ItemEnchantmentsComponent.CODEC.fieldOf("result").forGetter((recipe) -> recipe.result),
                        Codec.list(NBTIngredient.DISALLOW_EMPTY_CODEC).fieldOf("ingredients").forGetter((recipe) -> recipe.ingredients));
                Objects.requireNonNull(recipeFactory);
                return var10000.apply(instance, recipeFactory::create);
            });

            PacketCodec<ByteBuf, String> var10001 = PacketCodecs.STRING;
            Function<T, String> var10002 = (recipe) -> recipe.group;

            PacketCodec<RegistryByteBuf, List<NBTIngredient>> var10003 = PacketCodecs.registryCodec(Codec.list(NBTIngredient.DISALLOW_EMPTY_CODEC));
            Function<T, List<NBTIngredient>> var10004 = (recipe) -> recipe.ingredients;

            PacketCodec<RegistryByteBuf, ItemEnchantmentsComponent> var10005 = ItemEnchantmentsComponent.PACKET_CODEC;
            Function<T, ItemEnchantmentsComponent> var10006 = (recipe) -> recipe.enchantments;

            PacketCodec<RegistryByteBuf, ItemEnchantmentsComponent> var10007 = ItemEnchantmentsComponent.PACKET_CODEC;
            Function<T, ItemEnchantmentsComponent> var10008 = (recipe) -> recipe.result;

            Objects.requireNonNull(recipeFactory);
            this.packetCodec = PacketCodec.tuple(var10001, var10002, var10005, var10006, var10007, var10008, var10003, var10004, recipeFactory::create);
        }

        public MapCodec<T> codec() {
            return this.codec;
        }

        public PacketCodec<RegistryByteBuf, T> packetCodec() {
            return this.packetCodec;
        }
    }
}
