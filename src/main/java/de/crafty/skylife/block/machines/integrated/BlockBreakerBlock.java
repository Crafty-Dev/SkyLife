package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.lifecompat.util.LifeCompatMenuHelper;
import de.crafty.skylife.blockentities.machines.integrated.BlockBreakerBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockBreakerBlock extends BaseEnergyBlock {

    public static final MapCodec<BlockBreakerBlock> CODEC = simpleCodec(BlockBreakerBlock::new);

    public static final DirectionProperty FACING = BaseEnergyBlock.FACING;

    public BlockBreakerBlock(Properties properties) {
        super(properties, Type.CONSUMER, EnergyUnitConverter.kiloVP(10.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(BaseEnergyBlock.IO_TOP, IOMode.INPUT)
                .setValue(BaseEnergyBlock.IO_BOTTOM, IOMode.INPUT)
                .setValue(BaseEnergyBlock.IO_LEFT, IOMode.INPUT)
                .setValue(BaseEnergyBlock.IO_RIGHT, IOMode.INPUT)
                .setValue(BaseEnergyBlock.IO_BACK, IOMode.INPUT)
        );
    }

    @Override
    public List<IOMode> validIOModes() {
        return List.of(IOMode.INPUT, IOMode.NONE);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockBreakerBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.BLOCK_BREAKER, BlockBreakerBlockEntity::tick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, IO_TOP, IO_BOTTOM, IO_LEFT, IO_RIGHT, IO_BACK);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, (blockPlaceContext.getPlayer().isCrouching() || blockPlaceContext.getPlayer().isShiftKeyDown()) ? blockPlaceContext.getNearestLookingDirection() : blockPlaceContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player
            player, BlockHitResult blockHitResult) {
        InteractionResult result = super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        if (result != InteractionResult.PASS)
            return result;

        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof BlockBreakerBlockEntity blockBreaker) {
            LifeCompatMenuHelper.openMenuAndSendPosition((ServerPlayer) player, blockBreaker);
            //TODO award Stats
        }

        return InteractionResult.CONSUME;
    }


    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        Containers.dropContentsOnDestroy(blockState, blockState2, level, blockPos);
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

}
