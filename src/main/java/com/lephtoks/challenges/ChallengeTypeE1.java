package com.lephtoks.challenges;

import com.lephtoks.components.ChallengeComponent;
import net.minecraft.item.ItemStack;

public interface ChallengeTypeE1<T> extends ChallengeType{
    public boolean matches(ChallengeComponent challenge, T entry);
    public default boolean trigger(ItemStack origin, ChallengeComponent challenge, T entry, float add) {
        if (matches(challenge, entry)) {
            challenge.addValue(origin, add);
            return true;
        }
        return false;
    }
}
