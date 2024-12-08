package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.lifecompat.util.LifeCompatMenuHelper;
import de.crafty.skylife.block.machines.AbstractUpgradableMachine;
import de.crafty.skylife.block.machines.IUpgradableMachine;
import de.crafty.skylife.blockentities.machines.integrated.FluxFurnaceBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluxFurnaceBlock extends AbstractUpgradableMachine<FluxFurnaceBlockEntity> {

    public static final MapCodec<FluxFurnaceBlock> CODEC = simpleCodec(FluxFurnaceBlock::new);

    public static final DirectionProperty FACING = BaseEnergyBlock.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty UPGRADED = BooleanProperty.create("upgraded");

    public FluxFurnaceBlock(Properties properties) {
        super(properties, Type.CONSUMER, EnergyUnitConverter.kiloVP(30.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(IO_TOP, IOMode.INPUT)
                .setValue(IO_BOTTOM, IOMode.INPUT)
                .setValue(IO_LEFT, IOMode.INPUT)
                .setValue(IO_RIGHT, IOMode.INPUT)
                .setValue(IO_BACK, IOMode.INPUT)
                .setValue(ACTIVE, false)
                .setValue(UPGRADED, false)
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
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FluxFurnaceBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.FLUX_FURNACE, FluxFurnaceBlockEntity::tick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IO_TOP, IO_BOTTOM, IO_LEFT, IO_RIGHT, IO_BACK, FACING, ACTIVE, UPGRADED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        InteractionResult result = super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        if (result != InteractionResult.PASS)
            return result;

        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof FluxFurnaceBlockEntity fluxFurnaceBlockEntity)
            LifeCompatMenuHelper.openMenuAndSendPosition((ServerPlayer) player, fluxFurnaceBlockEntity);

        return InteractionResult.CONSUME;
    }

    @Override
    public List<Item> validUpgradeItems() {
        return List.of(ItemRegistry.UPGRADE_MODULE);
    }

    @Override
    public void onUpgrade(Level level, BlockState machine, BlockPos blockPos, ItemStack upgradeStack, FluxFurnaceBlockEntity blockEntity) {
        blockEntity.setPerformanceMode(true);
        level.setBlock(blockPos, machine.setValue(UPGRADED, true), Block.UPDATE_ALL);
    }

    @Override
    public @Nullable Item getCurrentUpgrade(BlockState machine) {
        return machine.getValue(UPGRADED) ? ItemRegistry.UPGRADE_MODULE : null;
    }

    @Override
    public Class<FluxFurnaceBlockEntity> getMachineBE() {
        return FluxFurnaceBlockEntity.class;
    }

    @Override
    protected Property<? extends Comparable<?>> getUpgradeProperty() {
        return UPGRADED;
    }


    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.is(this)) {
            super.onRemove(blockState, level, blockPos, blockState2, bl);
            return;
        }

        Containers.dropContentsOnDestroy(blockState, blockState2, level, blockPos);

        Item currentUpgrade = this.getCurrentUpgrade(blockState);
        if (currentUpgrade != null)
            Block.popResource(level, blockPos, new ItemStack(currentUpgrade));

        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }
}
