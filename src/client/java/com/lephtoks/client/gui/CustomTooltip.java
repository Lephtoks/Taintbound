package com.lephtoks.client.gui;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipAppender;

public abstract class CustomTooltip<T extends TooltipAppender> implements TooltipComponent {
    public CustomTooltip(T tooltipAppender) {};
}
