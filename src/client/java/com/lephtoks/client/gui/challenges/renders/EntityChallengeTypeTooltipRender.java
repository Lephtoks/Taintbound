package com.lephtoks.client.gui.challenges.renders;

import com.lephtoks.challenges.EntityKillChallengeType;
import com.lephtoks.client.gui.challenges.ChallengeTooltipComponent;
import com.lephtoks.client.gui.challenges.ChallengeTypeTooltipRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityChallengeTypeTooltipRender extends ChallengeTypeTooltipRender {
    @Override
    public void renderSpecial(ChallengeTooltipComponent component, TextRenderer textRenderer, DrawContext context, int x, int y, int tick) {
        if (component.component.type() instanceof EntityKillChallengeType(EntityType<?> entityType) &&
            entityType.create(MinecraftClient.getInstance().world) instanceof LivingEntity livingEntity) {
                float height = entityType.getHeight();
                float width = entityType.getWidth();
                InventoryScreen.drawEntity(context, (float) x, (float) y, 20 / (float) Math.sqrt(height * height + 2 * width * width), new Vector3f(), new Quaternionf().rotateZ((float) Math.PI).rotateX((float) Math.sin(tick * 0.05) * 0.5f).rotateY(tick * 0.05f), null, livingEntity);
        }
    }
}
