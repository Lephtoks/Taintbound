package com.lephtoks.client.gui.challenges;

import com.lephtoks.challenges.EntityKillChallengeType;
import com.lephtoks.challenges.ItemChallengeType;
import com.lephtoks.client.gui.challenges.renders.EntityChallengeTypeTooltipRender;
import com.lephtoks.client.gui.challenges.renders.ItemChallengeTypeTooltipRender;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ChallengeTypeTooltipRenders {
    static final Map<Identifier, ChallengeTypeTooltipRender> MAP = new HashMap<>();

    private static void register(Identifier identifier, ChallengeTypeTooltipRender render) {
        MAP.put(identifier, render);
        render.setSpecialText(identifier);
    }
    public static ChallengeTypeTooltipRender get(Identifier identifier) {
        return MAP.get(identifier);
    }

    static {
        register(ItemChallengeType.RENDER, new ItemChallengeTypeTooltipRender());
        register(EntityKillChallengeType.RENDER, new EntityChallengeTypeTooltipRender());
    }
}
