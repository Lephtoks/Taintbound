package com.lephtoks.client.gui;

import com.lephtoks.client.gui.challenges.ChallengeTooltipComponent;
import com.lephtoks.components.ChallengeComponent;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CustomTooltips {
    private static final Map<Identifier, Class<? extends CustomTooltip<?>>> MAP = new HashMap<>();
    static {
        MAP.put(ChallengeComponent.TOOLTIP, ChallengeTooltipComponent.class);
    }
    public static Class<? extends CustomTooltip<?>> get(Identifier identifier) {
        return MAP.get(identifier);
    }
}
