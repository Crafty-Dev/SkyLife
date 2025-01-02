package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.fluid.block.BaseFluidEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.lifecompat.util.LifeCompatMenuHelper;
import de.crafty.skylife.blockentities.machines.integrated.SolidFluidMergerBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.FluidRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SolidFluidMergerBlock extends BaseFluidEnergyBlock {

    public static final MapCodec<SolidFluidMergerBlock> CODEC = simpleCodec(SolidFluidMergerBlock::new);


    public static final BooleanProperty ENERGY = BooleanProperty.create("energy");

    public SolidFluidMergerBlock(Properties properties) {
        super(properties, Type.CONSUMER, EnergyUnitConverter.kiloVP(10.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(IO_TOP, IOMode.INPUT)
                .setValue(IO_BOTTOM, IOMode.INPUT)
                .setValue(IO_LEFT, IOMode.INPUT)
                .setValue(IO_RIGHT, IOMode.INPUT)
                .setValue(IO_FRONT, IOMode.INPUT)
                .setValue(IO_BACK, IOMode.INPUT)
                .setValue(ENERGY, false)
        );
    }

    @Override
    public List<IOMode> validIOModes() {
        return List.of(IOMode.INPUT, IOMode.NONE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IO_TOP, IO_BOTTOM, IO_LEFT, IO_RIGHT, IO_FRONT, IO_BACK, ENERGY);
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
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolidFluidMergerBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.SOLID_FLUID_MERGER, SolidFluidMergerBlockEntity::tick);
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {

        InteractionResult result = super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);

        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        if(result != InteractionResult.PASS)
            return result;

        if(level.getBlockEntity(blockPos) instanceof SolidFluidMergerBlockEntity merger) {
            LifeCompatMenuHelper.openMenuAndSendPosition((ServerPlayer) player, merger);
            //TODO award stats

            return InteractionResult.CONSUME;
        }

        return result;
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if(blockState.is(blockState2.getBlock())) {
            super.onRemove(blockState, level, blockPos, blockState2, bl);
            return;
        }
        Containers.dropContentsOnDestroy(blockState, blockState2, level, blockPos);

        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }


    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        InteractionResult result = super.useItemOn(stack, blockState, level, blockPos, player, interactionHand, blockHitResult);

        if(!result.consumesAction() && stack.is(ItemRegistry.MACHINE_KEY))
            return this.tryChangeIO(level, blockPos, blockState, player, blockHitResult.getDirection()) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;


        return result;
    }
}
