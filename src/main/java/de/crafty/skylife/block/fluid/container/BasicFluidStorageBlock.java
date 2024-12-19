package de.crafty.skylife.block.fluid.container;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.api.fluid.logistic.container.IFluidContainer;
import de.crafty.lifecompat.fluid.block.BaseFluidContainerBlock;
import de.crafty.skylife.blockentities.fluid.BasicFluidStorageBlockEntity;
import de.crafty.skylife.registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BasicFluidStorageBlock extends BaseFluidContainerBlock {

    public static final MapCodec<BasicFluidStorageBlock> CODEC = simpleCodec(BasicFluidStorageBlock::new);


    private static final VoxelShape BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape TOP = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    private static final VoxelShape CORE = Block.box(2.0D, 1.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public BasicFluidStorageBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.or(BASE, TOP, CORE);
    }

    @Override
    public List<Direction> getFluidCompatableSides() {
        return List.of(Direction.values());
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
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BasicFluidStorageBlockEntity(blockPos, blockState);
    }
}
