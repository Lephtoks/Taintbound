package com.lephtoks.client.network;

import com.lephtoks.TaintboundMod;
import com.lephtoks.blockentities.TaintedTableBlockEntity;
import com.lephtoks.network.UpdateTaintedTableS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;

public class ClientTaintboundPacketHandler {
    public static void register() {
        PayloadTypeRegistry.playS2C().register(UpdateTaintedTableS2CPacket.ID, UpdateTaintedTableS2CPacket.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(UpdateTaintedTableS2CPacket.ID, (payload, context) -> {
            ClientWorld world = context.client().world;
            if (world != null) {
                if (world.getBlockEntity(payload.pos()) instanceof TaintedTableBlockEntity tet) {
                    tet.getInventory().clear();
                    tet.readNbt(payload.nbtCompound(), world.getRegistryManager());
                };
            }
        });
    }
}
