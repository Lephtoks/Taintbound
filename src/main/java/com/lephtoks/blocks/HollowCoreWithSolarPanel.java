package com.lephtoks.blocks;

import com.lephtoks.blockentities.HollowCoreBlockEntity;
import com.lephtoks.blockentities.TaintedBlockEntityTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HollowCoreWithSolarPanel extends AbstractHollowCore {
    public static final MapCodec<HollowCoreWithSolarPanel> CODEC = createCodec(HollowCoreWithSolarPanel::new);
    public static final BooleanProperty INVERTED = DaylightDetectorBlock.INVERTED;
    public HollowCoreWithSolarPanel(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(INVERTED, false));
    }
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(INVERTED);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, TaintedBlockEntityTypes.HOLLOW_CORE_WITH_DAYLIGHT_SENSOR, HollowCoreBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.isSneaking()) {
            world.setBlockState(pos, state.cycle(INVERTED));
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public boolean isWorking(World world, BlockState state) {
        return world.getTimeOfDay() % 24000 < 12000 == !state.get(INVERTED);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HollowCoreBlockEntity(TaintedBlockEntityTypes.HOLLOW_CORE_WITH_DAYLIGHT_SENSOR, pos, state);
    }
}
