package com.lephtoks.tooltip;

import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.util.List;

public record TooltipText(Identifier tooltipIdentifier, TooltipAppender tooltipAppender) implements Text, OrderedText {

    @Override
    public Style getStyle() {
        return null;
    }

    @Override
    public TextContent getContent() {
        return null;
    }

    @Override
    public List<Text> getSiblings() {
        return List.of();
    }

    @Override
    public OrderedText asOrderedText() {
        return this;
    }

    @Override
    public boolean accept(CharacterVisitor visitor) {
        return true;
    }
}
