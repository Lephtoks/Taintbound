package com.lephtoks.network;

import com.lephtoks.TaintboundMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record UpdateTaintedTableS2CPacket(NbtCompound nbtCompound, BlockPos pos) implements CustomPayload {

    public static final Identifier IDENTIFIER = Identifier.of(TaintboundMod.MOD_ID, "update_tainted_table");
    public static final Id<UpdateTaintedTableS2CPacket> ID = new Id<>(IDENTIFIER);
    public static final PacketCodec<RegistryByteBuf, UpdateTaintedTableS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND, UpdateTaintedTableS2CPacket::nbtCompound,
            BlockPos.PACKET_CODEC, UpdateTaintedTableS2CPacket::pos,
            UpdateTaintedTableS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
