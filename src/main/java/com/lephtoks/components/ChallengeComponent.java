package com.lephtoks.components;

import com.lephtoks.TaintboundMod;
import com.lephtoks.challenges.ChallengeType;
import com.lephtoks.challenges.ChallengeTypes;
import com.lephtoks.challenges.ItemChallengeType;
import com.lephtoks.tooltip.TooltipText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record ChallengeComponent(boolean showInTooltip, ChallengeType type, float max, float current, RegistryEntry<Enchantment> target, int targetAdder) implements TooltipAppender {
    public static final Codec<ChallengeComponent> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(ChallengeComponent::showInTooltip),
            ChallengeType.CODEC.fieldOf("trigger").forGetter(ChallengeComponent::type),
            Codec.FLOAT.optionalFieldOf("max", 15f).forGetter(ChallengeComponent::max),
            Codec.FLOAT.optionalFieldOf("duration", 0f).forGetter(ChallengeComponent::current),
            Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(ChallengeComponent::target),
            Codec.INT.optionalFieldOf("adder", -1).forGetter(ChallengeComponent::targetAdder)).apply(instance, ChallengeComponent::new));
    public static final PacketCodec<? super RegistryByteBuf, ChallengeComponent> PACKET_CODEC;

    public float remain() {
        return max - current;
    }
    public float addValue(ItemStack origin, float val) {
        float result = this.current + val;
        if (result >= this.max) {
            this.done(origin);
            return 0f;
        }
        origin.set(TaintedEnchantmentsDataComponentTypes.CHALLENGE, new ChallengeComponent(this.showInTooltip, this.type, this.max, result, this.target, this.targetAdder));
        return remain();
    }
    public void done(ItemStack origin) {
        var set = origin.getEnchantments();
        var builder = new ItemEnchantmentsComponent.Builder(set);
        builder.set(this.target, set.getLevel(target) + this.targetAdder);
        EnchantmentHelper.set(origin, builder.build());
        origin.remove(TaintedEnchantmentsDataComponentTypes.CHALLENGE);
    }

    public static List<ItemStack> getActiveComponents(PlayerEntity player) {
        List<ItemStack> list = new ArrayList<>(6);

        PlayerInventory inventory = player.getInventory();
        Consumer<ItemStack> add = (item) -> {
            if (item.contains(TaintedEnchantmentsDataComponentTypes.CHALLENGE)) list.add(item);
        };

        add.accept(inventory.getArmorStack(0));
        add.accept(inventory.getArmorStack(1));
        add.accept(inventory.getArmorStack(2));
        add.accept(inventory.getArmorStack(3));
        add.accept(inventory.getMainHandStack());
        add.accept(inventory.offHand.get(0));
        return list;
    }


    static {
        PACKET_CODEC =
                PacketCodec.tuple(
                PacketCodecs.BOOL, ChallengeComponent::showInTooltip,
                ChallengeTypes.PACKET_CODEC, ChallengeComponent::type,
                PacketCodecs.FLOAT, ChallengeComponent::max,
                PacketCodecs.FLOAT, ChallengeComponent::current,
                PacketCodecs.registryEntry(RegistryKeys.ENCHANTMENT), ChallengeComponent::target,
                PacketCodecs.INTEGER, ChallengeComponent::targetAdder,
                ChallengeComponent::new
        );
    }
    public static final Identifier TOOLTIP = Identifier.of(TaintboundMod.MOD_ID, "challenge_tooltip");
    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(new TooltipText(TOOLTIP, this));
    }

    public static class Builder {
        private final ChallengeType challengeType;
        public float costAtFirstLevel;
        public int levelsToAdd;
        public static Builder EMPTY = new Builder(new ItemChallengeType(Ingredient.ofItems(Items.AIR)), 0, 0);
        public Builder(ChallengeType challengeType, float costAtFirstLevel, int levelsToAdd) {
            this.challengeType = challengeType;
            this.costAtFirstLevel = costAtFirstLevel;
            this.levelsToAdd = levelsToAdd;
        }
        public Builder copy() {
            return new Builder(this.challengeType, this.costAtFirstLevel, this.levelsToAdd);
        }
        public ChallengeComponent build(boolean showInTooltip, RegistryEntry<Enchantment> entry) {
            return new ChallengeComponent(showInTooltip, this.challengeType, this.costAtFirstLevel, 0, entry, this.levelsToAdd);
        }
    }
}
