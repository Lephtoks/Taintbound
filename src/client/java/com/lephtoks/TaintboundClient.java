package com.lephtoks;

import com.lephtoks.blocks.TaintedBlocks;
import com.lephtoks.client.EntityRenderers;
import com.lephtoks.client.gui.screens.HollowCoreScreen;
import com.lephtoks.client.network.ClientTaintboundPacketHandler;
import com.lephtoks.client.particles.ChaoticSandParticle;
import com.lephtoks.client.particles.CorruptionParticle;
import com.lephtoks.particles.ParticleTypes;
import com.lephtoks.screen.TaintboundScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class TaintboundClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRenderers.initialize();
		ClientTaintboundPacketHandler.register();
		ParticleFactoryRegistry.getInstance().register(ParticleTypes.CORRUPTION_PARTICLE, CorruptionParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ParticleTypes.CHAOTIC_SAND, ChaoticSandParticle.Factory::new);
		BlockRenderLayerMap.INSTANCE.putBlock(TaintedBlocks.HOLLOW_CORE, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(TaintedBlocks.HOLLOW_CORE_WITH_DAYLIGHT_SENSOR, RenderLayer.getTranslucent());

		HandledScreens.register(TaintboundScreenHandlerTypes.HOLLOW_CORE, HollowCoreScreen::new);
	}
}