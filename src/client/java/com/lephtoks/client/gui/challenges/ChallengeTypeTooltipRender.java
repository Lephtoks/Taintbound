package com.lephtoks.client.gui.challenges;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ChallengeTypeTooltipRender {
    Text translatable;
    public abstract void renderSpecial(ChallengeTooltipComponent component, TextRenderer textRenderer, DrawContext context, int x, int y, int tick);
    public void setSpecialText(Identifier identifier) {
        translatable = Text.translatable("challenges.taintbound." + identifier.getPath());
    };
    public Text getSpecialText(ChallengeTooltipComponent component) {
        return translatable;
    }
}
