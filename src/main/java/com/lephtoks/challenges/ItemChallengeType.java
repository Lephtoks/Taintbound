package com.lephtoks.challenges;

import com.lephtoks.TaintboundMod;
import com.lephtoks.challenges.serializers.ChallengeSerializers;
import com.lephtoks.components.ChallengeComponent;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public record ItemChallengeType(Ingredient item) implements ChallengeTypeE1<ItemStack> {
    public static final Identifier RENDER = Identifier.of(TaintboundMod.MOD_ID, "item");

    @Override
    public Identifier getChallengeTooltipRenderIdentifier() { return RENDER; }

    @Override
    public ChallengeSerializer getSerializer() {
        return ChallengeSerializers.ITEM_CHALLENGE_SERIALIZER;
    }

    @Override
    public boolean matches(ChallengeComponent challenge, ItemStack entry) {
        return item.test(entry);
    }

    public static class Serializer implements ChallengeSerializer {
        final MapCodec<ItemChallengeType> codec;
        final PacketCodec<RegistryByteBuf, ItemChallengeType> packetCodec;
        public Serializer() {

            this.codec = RecordCodecBuilder.mapCodec((instance) -> {
                Products.P1<RecordCodecBuilder.Mu<ItemChallengeType>, Ingredient> var10000 = instance.group(
                        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("item").forGetter(ItemChallengeType::item)
                );

                return var10000.apply(instance, ItemChallengeType::new);
            });

            this.packetCodec = PacketCodec.tuple(

                    Ingredient.PACKET_CODEC,
                    ItemChallengeType::item,

            ItemChallengeType::new);
        }
        @Override
        public MapCodec<? extends ChallengeType> getCodec() {
            return this.codec;
        }

        @Override
        public PacketCodec<? super RegistryByteBuf, ? extends ChallengeType> getPacketCodec() {
            return this.packetCodec;
        }
    }
}
