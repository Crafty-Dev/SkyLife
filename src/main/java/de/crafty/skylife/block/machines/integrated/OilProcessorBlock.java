package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.fluid.block.BaseFluidEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.lifecompat.util.LifeCompatMenuHelper;
import de.crafty.skylife.blockentities.machines.integrated.OilProcessorBlockEntity;
import de.crafty.skylife.blockentities.machines.integrated.SolidFluidMergerBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.FluidRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class OilProcessorBlock extends BaseFluidEnergyBlock {

    public static final MapCodec<OilProcessorBlock> CODEC = simpleCodec(OilProcessorBlock::new);

    private static final VoxelShape SHAPE_NORTH = OilProcessorBlock.makeShapeNorth();
    private static final VoxelShape SHAPE_EAST = OilProcessorBlock.makeShapeEast();
    private static final VoxelShape SHAPE_SOUTH = OilProcessorBlock.makeShapeSouth();
    private static final VoxelShape SHAPE_WEST = OilProcessorBlock.makeShapeWest();

    public static final EnumProperty<Direction> FACING = BaseEnergyBlock.HORIZONTAL_FACING;
    public static final BooleanProperty ENERGY = BooleanProperty.create("energy");

    public OilProcessorBlock(Properties properties) {
        super(properties, Type.CONTAINER, EnergyUnitConverter.kiloVP(25.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(IO_BOTTOM, IOMode.INPUT)
                .setValue(IO_RIGHT, IOMode.INPUT)
                .setValue(IO_BACK, IOMode.INPUT)
                .setValue(ENERGY, false)
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return switch (blockState.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IO_BOTTOM, IO_RIGHT, IO_BACK, ENERGY, FACING);
    }

    @Override
    public List<Direction> getFluidCompatableSides() {
        return List.of(Direction.SOUTH, Direction.WEST, Direction.DOWN);
    }

    @Override
    public boolean allowBucketFill(BlockState blockState) {
        return true;
    }

    @Override
    public boolean allowBucketEmpty(BlockState blockState) {
        return true;
    }

    @Override
    public Optional<SoundEvent> getBucketEmptySound(Fluid fluid, Level level, BlockPos blockPos, BlockState blockState) {
        return fluid == Fluids.LAVA || fluid == FluidRegistry.MOLTEN_OBSIDIAN || fluid == FluidRegistry.OIL ? Optional.of(SoundEvents.BUCKET_EMPTY_LAVA) : Optional.of(SoundEvents.BUCKET_EMPTY);
    }


    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new OilProcessorBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.OIL_PROCESSOR, OilProcessorBlockEntity::tick);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        InteractionResult result = super.useItemOn(stack, blockState, level, blockPos, player, interactionHand, blockHitResult);


        if (!result.consumesAction() && stack.is(ItemRegistry.MACHINE_KEY) && !(player.isCrouching() || player.isShiftKeyDown()))
            return this.tryChangeIO(level, blockPos, blockState, player, blockHitResult.getDirection()) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;


        return result;
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        InteractionResult result = super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);

        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        if (result != InteractionResult.PASS)
            return result;

        if (level.getBlockEntity(blockPos) instanceof OilProcessorBlockEntity processorBlockEntity) {
            LifeCompatMenuHelper.openMenuAndSendPosition((ServerPlayer) player, processorBlockEntity);
            //TODO award stats

            return InteractionResult.CONSUME;
        }

        return result;
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        Containers.dropContentsOnDestroy(blockState, blockState2, level, blockPos);
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    public static VoxelShape makeShapeNorth() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0, 0.1875, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0625, 0, 0.25, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.0625, 0.5625, 1, 0.6875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.0625, 0.5, 1, 0.125, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.625, 0.5, 1, 0.6875, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.125, 0.5, 1, 0.625, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.125, 0.5, 0.3125, 0.625, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0.53125, 0.375, 0.625, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.875, 0.125, 0.53125, 0.9375, 0.625, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.5625, 0.53125, 0.875, 0.625, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.125, 0.53125, 0.875, 0.1875, 0.5625), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeShapeEast() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0, 1, 0.9375, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.1875, 1, 0.9375, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.25, 0.4375, 0.6875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.0625, 0.25, 0.5, 0.125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.625, 0.25, 0.5, 0.6875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0.9375, 0.5, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0.25, 0.5, 0.625, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0.3125, 0.46875, 0.625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0.875, 0.46875, 0.625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.5625, 0.375, 0.46875, 0.625, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0.375, 0.46875, 0.1875, 0.875), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeShapeSouth() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.0625, 0, 1, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0.0625, 0, 0.8125, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0, 0.75, 0.6875, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.4375, 0.75, 0.125, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.4375, 0.75, 0.6875, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0.4375, 0.0625, 0.625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.125, 0.4375, 0.75, 0.625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.125, 0.4375, 0.6875, 0.625, 0.46875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.125, 0.4375, 0.125, 0.625, 0.46875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.5625, 0.4375, 0.625, 0.625, 0.46875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.4375, 0.625, 0.1875, 0.46875), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeShapeWest() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.8125, 1, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.75, 1, 0.9375, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.0625, 0, 1, 0.6875, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.0625, 0, 0.5625, 0.125, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.625, 0, 0.5625, 0.6875, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.125, 0, 0.5625, 0.625, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.125, 0.6875, 0.5625, 0.625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0.125, 0.625, 0.5625, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0.125, 0.0625, 0.5625, 0.625, 0.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0.5625, 0.125, 0.5625, 0.625, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.53125, 0.125, 0.125, 0.5625, 0.1875, 0.625), BooleanOp.OR);

        return shape;
    }
}
