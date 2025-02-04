package com.lephtoks.mixinaccessors;

public interface PlayerDataAccessor {
    double getHeat();
    void setHeat(double value);

    default void addHeat(double value) {
        setHeat(getHeat() + value);
    };

    public boolean inGoldRatio(float baseTime, float delta);
}
