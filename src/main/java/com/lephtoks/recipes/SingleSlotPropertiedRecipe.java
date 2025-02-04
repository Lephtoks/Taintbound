package com.lephtoks.recipes;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.function.Function;

public abstract class SingleSlotPropertiedRecipe implements Recipe<OnePropertiedItemRecipeInput> {

    protected final RecipeType<?> type;
    protected final int cost;
    protected final NBTIngredient ingredient;
    protected final ItemStack result;
    protected final RecipeSerializer<?> serializer;
    protected final String group;

    public SingleSlotPropertiedRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, String group, NBTIngredient ingredient,
                            ItemStack result,
                            int cost) {

        this.type = type;
        this.serializer = serializer;
        this.ingredient = ingredient;
        this.group = group;

        this.cost = cost;
        this.result = result;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public boolean matches(OnePropertiedItemRecipeInput input, World world) {
        return input.property() >= this.cost && ingredient.test(input.getStackInSlot(0));
    }
    @Override
    public RecipeType<?> getType() {
        return type;
    }

    @Override
    public ItemStack craft(OnePropertiedItemRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.ingredient.intoIngredient());
        return defaultedList;
    }
    public NBTIngredient getIngredient() {
        return this.ingredient;
    }


    public interface RecipeFactory<T extends SingleSlotPropertiedRecipe> {
        T create(String group, NBTIngredient ingredient, ItemStack itemStack, int cost);
    }

    public static class Serializer<T extends SingleSlotPropertiedRecipe> implements RecipeSerializer<T> {
        final RecipeFactory<T> recipeFactory;
        private final MapCodec<T> codec;
        private final PacketCodec<RegistryByteBuf, T> packetCodec;

        public Serializer(RecipeFactory<T> recipeFactory) {
            this.recipeFactory = recipeFactory;
            this.codec = RecordCodecBuilder.mapCodec((instance) -> {
                Products.P4<RecordCodecBuilder.Mu<T>, String, NBTIngredient, ItemStack, Integer> var10000 = instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter((recipe) -> recipe.group),
                        NBTIngredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter((recipe) -> recipe.ingredient),
                        ItemStack.CODEC.fieldOf("result").forGetter((recipe) -> recipe.result),
                        Codec.INT.fieldOf("cost").forGetter((recipe) -> recipe.cost));
                Objects.requireNonNull(recipeFactory);
                return var10000.apply(instance, recipeFactory::create);
            });

            PacketCodec<ByteBuf, String> var10001 = PacketCodecs.STRING;
            Function<T, String> var10002 = (recipe) -> recipe.group;

            PacketCodec<ByteBuf, NBTIngredient> var10003 = PacketCodecs.codec(NBTIngredient.DISALLOW_EMPTY_CODEC);
            Function<T, NBTIngredient> var10004 = (recipe) -> recipe.ingredient;

            PacketCodec<RegistryByteBuf, ItemStack> var10005 = ItemStack.PACKET_CODEC;
            Function<T, ItemStack> var10006 = (recipe) -> recipe.result;

            PacketCodec<ByteBuf, Integer> var10007 = PacketCodecs.INTEGER;
            Function<T, Integer> var10008 = (recipe) -> recipe.cost;

            Objects.requireNonNull(recipeFactory);
            this.packetCodec = PacketCodec.tuple(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008, recipeFactory::create);
        }

        public MapCodec<T> codec() {
            return this.codec;
        }

        public PacketCodec<RegistryByteBuf, T> packetCodec() {
            return this.packetCodec;
        }
    }
}
