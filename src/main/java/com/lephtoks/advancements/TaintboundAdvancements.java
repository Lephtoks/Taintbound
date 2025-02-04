package com.lephtoks.advancements;

import com.lephtoks.TaintboundMod;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TaintboundAdvancements {
    public static final HeatValueCriterion HEAT_VALUE = register("heat_value", new HeatValueCriterion());
    public static final GoldRatioCriterion GOLD_RATIO = register("gold_ratio", new GoldRatioCriterion());

    public static <T extends Criterion<?>> T register(String id, T criterion) {
        return (Registry.register(Registries.CRITERION, Identifier.of(TaintboundMod.MOD_ID, id), criterion));
    }
    public static void init() {

    }
}
