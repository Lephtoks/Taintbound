package com.lephtoks.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public record BrokenEnchantmentAbilityComponent(boolean showInTooltip) implements TooltipAppender {
    public static final Codec<BrokenEnchantmentAbilityComponent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(BrokenEnchantmentAbilityComponent::showInTooltip)).apply(instance, BrokenEnchantmentAbilityComponent::new);
    });
    public static final PacketCodec<ByteBuf, BrokenEnchantmentAbilityComponent> PACKET_CODEC;
    private static final Text TOOLTIP_TEXT;

    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (this.showInTooltip) {
            tooltip.accept(TOOLTIP_TEXT);
        }

    }

    public boolean showInTooltip() {
        return this.showInTooltip;
    }

    static {
        PACKET_CODEC = PacketCodecs.BOOL.xmap(BrokenEnchantmentAbilityComponent::new, BrokenEnchantmentAbilityComponent::showInTooltip);
        TOOLTIP_TEXT = Text.translatable("item.taintbound.broken_enchantment_ability").formatted(Formatting.RED);
    }
}
