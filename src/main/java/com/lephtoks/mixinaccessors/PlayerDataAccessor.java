package com.lephtoks.mixinaccessors;

public interface PlayerDataAccessor {
    double taintedEnchantments$getHeat();
    void taintedEnchantments$setHeat(double value);

    default void addHeat(double value) {
        taintedEnchantments$setHeat(taintedEnchantments$getHeat() + value);
    }

    boolean taintedEnchantments$inGoldRatio(float baseTime, float delta);

    void taintedEnchantments$removeAttackSpeedBuff();
}
