package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.skylife.block.machines.AbstractUpgradableMachine;
import de.crafty.skylife.blockentities.machines.integrated.SolarPanelBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarPanelBlock extends AbstractUpgradableMachine<SolarPanelBlockEntity> {

    public static final MapCodec<SolarPanelBlock> CODEC = simpleCodec(SolarPanelBlock::new);

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty UPGRADED = BooleanProperty.create("upgraded");

    private static final VoxelShape SHAPE = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 11.0F, 16.0F);

    private static final VoxelShape EXCLUDE_NORTH = Block.box(3.0F, 5.0F, 0.0F, 13.0F, 8.0F, 1.0F);
    private static final VoxelShape EXCLUDE_EAST = Block.box(0.0F, 5.0F, 3.0F, 1.0F, 8.0F, 13.0F);
    private static final VoxelShape EXCLUDE_SOUTH = Block.box(3.0F, 5.0F, 15.0F, 13.0F, 8.0F, 16.0F);
    private static final VoxelShape EXCLUDE_WEST = Block.box(15.0F, 5.0F, 3.0F, 16.0F, 8.0F, 13.0F);

    private static final VoxelShape EXCLUDED = Shapes.or(EXCLUDE_NORTH, EXCLUDE_EAST, EXCLUDE_SOUTH, EXCLUDE_WEST);

    public SolarPanelBlock(Properties properties) {
        super(properties, Type.PROVIDER, EnergyUnitConverter.kiloVP(20.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(IO_BOTTOM, IOMode.OUTPUT)
                .setValue(ACTIVE, false)
                .setValue(UPGRADED, false)
        );
    }

    @Override
    public List<IOMode> validIOModes() {
        return List.of(IOMode.OUTPUT, IOMode.NONE);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.join(SHAPE, EXCLUDED, BooleanOp.ONLY_FIRST);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolarPanelBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.SOLAR_PANEL, SolarPanelBlockEntity::tick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IO_BOTTOM, ACTIVE, UPGRADED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(IO_BOTTOM, IOMode.OUTPUT);
    }

    @Override
    public List<Item> validUpgradeItems() {
        return List.of(ItemRegistry.UPGRADE_MODULE);
    }

    @Override
    public void onUpgrade(Level level, BlockState machine, BlockPos blockPos, ItemStack upgradeStack, SolarPanelBlockEntity blockEntity) {
        blockEntity.setUpgraded(true);
        level.setBlock(blockPos, machine.setValue(UPGRADED, true), Block.UPDATE_ALL);
        //TODO Add sound to block and when upgrade applied
    }

    @Override
    public @Nullable Item getCurrentUpgrade(BlockState machine) {
        return machine.getValue(UPGRADED) ? ItemRegistry.UPGRADE_MODULE : null;
    }

    @Override
    public Class<SolarPanelBlockEntity> getMachineBE() {
        return SolarPanelBlockEntity.class;
    }

    @Override
    protected Property<? extends Comparable<?>> getUpgradeProperty() {
        return UPGRADED;
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {

        Item currentUpgrade = this.getCurrentUpgrade(blockState);
        if (currentUpgrade != null)
            Block.popResource(level, blockPos, new ItemStack(currentUpgrade));

        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }
}
