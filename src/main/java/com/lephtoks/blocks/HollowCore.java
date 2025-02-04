package com.lephtoks.blocks;

import com.lephtoks.blockentities.HollowCoreBlockEntity;
import com.lephtoks.blockentities.TaintedBlockEntityTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HollowCore extends AbstractHollowCore {
    public static final MapCodec<HollowCore> CODEC = createCodec(HollowCore::new);
    public static final BooleanProperty ACTIVE = Properties.ENABLED;
    public HollowCore(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
    }
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, TaintedBlockEntityTypes.HOLLOW_CORE, HollowCoreBlockEntity::tick);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean bl = state.get(ACTIVE);
            // Что-то изменилось
            if (bl != world.isReceivingRedstonePower(pos)) {
                world.setBlockState(pos, state.cycle(ACTIVE), 2);
            }
        }
    }

    @Override
    public boolean isWorking(World world, BlockState state) {
        return state.contains(HollowCore.ACTIVE) && state.get(HollowCore.ACTIVE);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HollowCoreBlockEntity(TaintedBlockEntityTypes.HOLLOW_CORE, pos, state);
    }
}
