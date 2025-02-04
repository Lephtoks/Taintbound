package com.lephtoks.client.gui.challenges.renders;

import com.lephtoks.challenges.ItemChallengeType;
import com.lephtoks.client.gui.challenges.ChallengeTooltipComponent;
import com.lephtoks.client.gui.challenges.ChallengeTypeTooltipRender;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class ItemChallengeTypeTooltipRender extends ChallengeTypeTooltipRender {
    @Override
    public void renderSpecial(ChallengeTooltipComponent component, TextRenderer textRenderer, DrawContext context, int x, int y, int tick) {
        if (component.component.type() instanceof ItemChallengeType(Ingredient item)) {
            var matchingStacks = item.getMatchingStacks();
            x-=6;
            y-=18;
            ItemStack itemStack = matchingStacks[(int) (tick*0.05 % matchingStacks.length)];
            context.drawGuiTexture(Identifier.ofVanilla("container/bundle/slot"), x-1, y-1, 0, 18, 20);
            context.drawItem(itemStack, x, y);
            context.drawItemInSlot(textRenderer, itemStack, x, y);
        }
    }
}
