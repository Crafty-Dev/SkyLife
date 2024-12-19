package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.fluid.block.BaseFluidEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.skylife.block.machines.AbstractUpgradableFluidMachine;
import de.crafty.skylife.blockentities.machines.integrated.FluidPumpBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.FluidRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FluidPumpBlock extends AbstractUpgradableFluidMachine<FluidPumpBlockEntity> {

    public static final MapCodec<FluidPumpBlock> CODEC = simpleCodec(FluidPumpBlock::new);

    public static final BooleanProperty ENERGY = BooleanProperty.create("energy");
    public static final BooleanProperty UPGRADED = BooleanProperty.create("upgraded");

    public FluidPumpBlock(Properties properties) {
        super(properties, Type.CONSUMER, EnergyUnitConverter.kiloVP(10.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(IO_FRONT, IOMode.INPUT)
                .setValue(IO_RIGHT, IOMode.INPUT)
                .setValue(IO_BACK, IOMode.INPUT)
                .setValue(IO_LEFT, IOMode.INPUT)
                .setValue(IO_TOP, IOMode.INPUT)
                .setValue(ENERGY, false)
                .setValue(UPGRADED, false)
        );
    }

    @Override
    public List<IOMode> validIOModes() {
        return List.of(IOMode.INPUT, IOMode.NONE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IO_LEFT, IO_RIGHT, IO_FRONT, IO_BACK, IO_TOP, ENERGY, UPGRADED);
    }

    @Override
    public List<Direction> getFluidCompatableSides() {
        return List.of(Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    }

    @Override
    public boolean allowBucketFill(BlockState blockState) {
        return true;
    }

    @Override
    public boolean allowBucketEmpty(BlockState blockState) {
        return false;
    }

    @Override
    public Optional<SoundEvent> getBucketEmptySound(Fluid fluid, Level level, BlockPos blockPos, BlockState blockState) {
        return fluid == Fluids.LAVA || fluid == FluidRegistry.MOLTEN_OBSIDIAN || fluid == FluidRegistry.OIL ? Optional.of(SoundEvents.BUCKET_EMPTY_LAVA) : Optional.of(SoundEvents.BUCKET_EMPTY);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FluidPumpBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.FLUID_PUMP, FluidPumpBlockEntity::tick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemInteractionResult result = super.useItemOn(stack, blockState, level, blockPos, player, interactionHand, blockHitResult);

        if(result == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION && stack.is(ItemRegistry.MACHINE_KEY))
            return this.tryChangeIO(level, blockPos, blockState, player, blockHitResult.getDirection()) ? ItemInteractionResult.sidedSuccess(level.isClientSide()) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;


        return result;
    }

    @Override
    protected Property<? extends Comparable<?>> getUpgradeProperty() {
        return UPGRADED;
    }

    @Override
    public List<Item> validUpgradeItems() {
        return List.of(ItemRegistry.UPGRADE_MODULE);
    }

    @Override
    public void onUpgrade(Level level, BlockState machine, BlockPos blockPos, ItemStack upgradeStack, FluidPumpBlockEntity blockEntity) {
        blockEntity.setUpgraded(true);
        level.setBlock(blockPos, machine.setValue(UPGRADED, true), Block.UPDATE_ALL);
        //TODO Add sound to block and when upgrade applied
    }

    @Override
    public @Nullable Item getCurrentUpgrade(BlockState machine) {
        return machine.getValue(UPGRADED) ? ItemRegistry.UPGRADE_MODULE : null;
    }

    @Override
    public Class<FluidPumpBlockEntity> getMachineBE() {
        return FluidPumpBlockEntity.class;
    }
}
