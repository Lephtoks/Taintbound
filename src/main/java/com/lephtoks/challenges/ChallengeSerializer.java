package com.lephtoks.challenges;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface ChallengeSerializer {

        MapCodec<? extends ChallengeType> getCodec();

        PacketCodec<? super RegistryByteBuf,? extends ChallengeType> getPacketCodec();
        
    }
