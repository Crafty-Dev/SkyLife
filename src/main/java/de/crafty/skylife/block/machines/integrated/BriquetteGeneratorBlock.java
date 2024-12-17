package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.skylife.blockentities.machines.integrated.BriquetteGeneratorBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import de.crafty.skylife.registry.TagRegistry;
import de.crafty.skylife.item.BriquettItem;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BriquetteGeneratorBlock extends BaseEnergyBlock implements WorldlyContainerHolder {

    public static final MapCodec<BriquetteGeneratorBlock> CODEC = simpleCodec(BriquetteGeneratorBlock::new);

    private static final VoxelShape AIR_0 = Block.box(2.0D, 0.5D, 2.0D, 14.0D, 14.0D, 14.0D);

    private static final VoxelShape AIR_1_NORTH = Block.box(2.0D, 1.0D, 0.0D, 14.0D, 13.0D, 2.0D);
    private static final VoxelShape AIR_NORTH = Shapes.or(AIR_0, AIR_1_NORTH);

    private static final VoxelShape AIR_1_EAST = Block.box(14.0D, 1.0D, 2.0D, 16.0D, 13.0D, 14.0D);
    private static final VoxelShape AIR_EAST = Shapes.or(AIR_0, AIR_1_EAST);

    private static final VoxelShape AIR_1_SOUTH = Block.box(2.0D, 1.0D, 14.0D, 14.0D, 13.0D, 16.0D);
    private static final VoxelShape AIR_SOUTH = Shapes.or(AIR_0, AIR_1_SOUTH);

    private static final VoxelShape AIR_1_WEST = Block.box(0.0D, 1.0D, 2.0D, 2.0D, 13.0D, 14.0D);
    private static final VoxelShape AIR_WEST = Shapes.or(AIR_0, AIR_1_WEST);

    private static final VoxelShape SHAPE_NORTH = Shapes.join(Shapes.block(), AIR_NORTH, BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_EAST = Shapes.join(Shapes.block(), AIR_EAST, BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_SOUTH = Shapes.join(Shapes.block(), AIR_SOUTH, BooleanOp.ONLY_FIRST);
    private static final VoxelShape SHAPE_WEST = Shapes.join(Shapes.block(), AIR_WEST, BooleanOp.ONLY_FIRST);

    public static final BooleanProperty WORKING = BooleanProperty.create("working");
    public static final DirectionProperty FACING = BaseEnergyBlock.HORIZONTAL_FACING;
    public static final EnumProperty<BriquetteType> BRIQUETTE_TYPE = EnumProperty.create("briquette", BriquetteType.class);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BriquetteGeneratorBlock(Properties properties) {
        super(properties, Type.PROVIDER, EnergyUnitConverter.kiloVP(10.0F));
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WORKING, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(BRIQUETTE_TYPE, BriquetteType.EMPTY)
                .setValue(POWERED, false)
                .setValue(IO_TOP, IOMode.OUTPUT)
        );
    }

    @Override
    public List<IOMode> validIOModes() {
        return List.of(IOMode.OUTPUT, IOMode.NONE);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
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
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (blockState.getValue(WORKING)) {
            double x = blockPos.getX() + 0.5D;
            double y = blockPos.getY();
            double z = blockPos.getZ() + 0.5D;
            if (randomSource.nextDouble() < 0.1) {
                level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = blockState.getValue(FACING);
            Direction.Axis axis = direction.getAxis();

            double horizontalRandom = randomSource.nextDouble() * 0.6 - 0.3;

            for(int i = -1; i <= 1; i += 2){
                double offsetX = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.25 * i : horizontalRandom;
                double offsetY = randomSource.nextDouble() * 6.0 / 16.0;
                double offsetZ = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.25 * i : horizontalRandom;
                level.addParticle(ParticleTypes.SMOKE, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);
                level.addParticle(ParticleTypes.FLAME, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WORKING, FACING, BRIQUETTE_TYPE, POWERED, IO_TOP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState()
                .setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite())
                .setValue(POWERED, blockPlaceContext.getLevel().hasNeighborSignal(blockPlaceContext.getClickedPos()));
    }

    @Override
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (level.isClientSide())
            return;

        boolean powered = blockState.getValue(POWERED);
        if (level.hasNeighborSignal(blockPos) && !powered) {
            level.setBlock(blockPos, blockState.setValue(POWERED, true), 2);

            this.enflame(level, blockPos, blockState);
        }
        if (!level.hasNeighborSignal(blockPos) && powered)
            level.setBlock(blockPos, blockState.setValue(POWERED, false), 2);

        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return blockState.getValue(WORKING) ? 15 : 0;
    }

    private boolean enflame(Level level, BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(BRIQUETTE_TYPE) == BriquetteType.EMPTY || blockState.getValue(WORKING))
            return false;

        level.setBlock(blockPos, blockState.setValue(WORKING, true), 3);
        SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BG_WORKING_STARTED, blockPos, level);
        return true;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!(level.getBlockEntity(blockPos) instanceof BriquetteGeneratorBlockEntity blockEntity))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if(!this.canInteractWithContent(blockState, blockPos, blockHitResult))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (itemStack.is(TagRegistry.BRIQUETTES) && blockState.getValue(BRIQUETTE_TYPE) == BriquetteType.EMPTY) {
            level.setBlock(blockPos, blockState.setValue(BRIQUETTE_TYPE, ((BriquettItem) itemStack.getItem()).getType()), 3);
            itemStack.consume(1, player);
            blockEntity.setBriquett(itemStack);
            SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BG_BRIQUETTE_CHANGE, blockPos, level);
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        if (itemStack.is(Items.FLINT_AND_STEEL)) {
            if (!this.enflame(level, blockPos, blockState))
                return ItemInteractionResult.sidedSuccess(level.isClientSide());

            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(interactionHand));
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        if (itemStack.is(Items.FIRE_CHARGE)) {
            if (!this.enflame(level, blockPos, blockState))
                return ItemInteractionResult.sidedSuccess(level.isClientSide());

            itemStack.consume(1, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        if(itemStack.is(ItemRegistry.MACHINE_KEY))
            return this.tryChangeIO(level, blockPos, blockState, player, blockHitResult.getDirection()) ? ItemInteractionResult.sidedSuccess(level.isClientSide()) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (!(level.getBlockEntity(blockPos) instanceof BriquetteGeneratorBlockEntity blockEntity))
            return InteractionResult.PASS;

        if (player.isCrouching() && blockState.getValue(BRIQUETTE_TYPE) != BriquetteType.EMPTY && !blockState.getValue(WORKING) && this.canInteractWithContent(blockState, blockPos, blockHitResult)) {
            level.setBlock(blockPos, blockState.setValue(BRIQUETTE_TYPE, BriquetteType.EMPTY), 3);
            player.addItem(blockEntity.getBriquette());

            blockEntity.setBriquett(ItemStack.EMPTY);
            level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.ROOTED_DIRT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F, true);

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if(!this.canInteractWithContent(blockState, blockPos, blockHitResult))
            return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);

        return InteractionResult.PASS;
    }

    private boolean canInteractWithContent(BlockState state, BlockPos pos, BlockHitResult hitResult){
        double x = hitResult.getLocation().x() - pos.getX();
        double y = hitResult.getLocation().y() - pos.getY();
        double z = hitResult.getLocation().z() - pos.getZ();

        boolean hitInside = (x >= 0.125F && x <= 0.9875F) && (y >= 0.03125F && y <= 0.9875F) && (z >= 0.125F && z <= 0.9875F);
        boolean hitFront = state.getValue(FACING) == hitResult.getDirection();

        return hitInside || hitFront;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        if (blockState.getValue(WORKING) || !(levelAccessor instanceof ServerLevel level))
            return;

        if (blockState.getValue(BRIQUETTE_TYPE) == BriquetteType.WOOD)
            Block.popResource(level, blockPos, new ItemStack(ItemRegistry.WOOD_BRIQUETTES));

        if (blockState.getValue(BRIQUETTE_TYPE) == BriquetteType.COAL)
            Block.popResource(level, blockPos, new ItemStack(ItemRegistry.COAL_BRIQUETTES));
    }

    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (blockState.getValue(WORKING) && entity instanceof LivingEntity && !entity.fireImmune())
            entity.hurt(level.damageSources().campfire(), 1.0F);

    }

    @Override
    public WorldlyContainer getContainer(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
        if (blockState.getValue(WORKING))
            return new EmptyContainer();

        return switch (blockState.getValue(BRIQUETTE_TYPE)) {
            case WOOD ->
                    new OutputContainer(blockState, levelAccessor, blockPos, new ItemStack(ItemRegistry.WOOD_BRIQUETTES));
            case COAL ->
                    new OutputContainer(blockState, levelAccessor, blockPos, new ItemStack(ItemRegistry.COAL_BRIQUETTES));
            default -> new InputContainer(blockState, levelAccessor, blockPos);
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BriquetteGeneratorBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.BRIQUETTE_GENERATOR, BriquetteGeneratorBlockEntity::tick);
    }

    public enum BriquetteType implements StringRepresentable {
        EMPTY,
        WOOD,
        COAL;

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }


    static class EmptyContainer extends SimpleContainer implements WorldlyContainer {

        public EmptyContainer() {
            super(0);
        }

        @Override
        public int[] getSlotsForFace(Direction direction) {
            return new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
            return false;
        }
    }

    static class InputContainer extends SimpleContainer implements WorldlyContainer {

        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;

        public InputContainer(BlockState blockState, LevelAccessor level, BlockPos pos) {
            super(1);
            this.state = blockState;
            this.level = level;
            this.pos = pos;
        }

        @Override
        public int[] getSlotsForFace(Direction direction) {
            return direction != Direction.DOWN ? new int[1] : new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
            System.out.println(itemStack.is(TagRegistry.BRIQUETTES));
            return !this.changed && direction != Direction.DOWN && this.state.getValue(BRIQUETTE_TYPE) == BriquetteType.EMPTY && itemStack.is(TagRegistry.BRIQUETTES);
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
            return false;
        }

        @Override
        public void setChanged() {
            if (!(this.level.getBlockEntity(this.pos) instanceof BriquetteGeneratorBlockEntity blockEntity))
                return;

            ItemStack stack = this.getItem(0);
            if (stack.isEmpty())
                return;

            this.changed = true;

            if (stack.is(TagRegistry.BRIQUETTES)) {
                BriquetteType type = ((BriquettItem) stack.getItem()).getType();
                BlockState blockState = this.state.setValue(BRIQUETTE_TYPE, type);
                blockEntity.setBriquett(stack);
                this.level.setBlock(this.pos, blockState, 3);
                SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BG_BRIQUETTE_CHANGE, this.pos, this.level);
                this.removeItemNoUpdate(0);
            }

        }
    }

    static class OutputContainer extends SimpleContainer implements WorldlyContainer {

        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;

        public OutputContainer(BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
            super(stack);
            this.state = state;
            this.level = level;
            this.pos = pos;
        }


        @Override
        public int[] getSlotsForFace(Direction direction) {
            return direction == Direction.DOWN ? new int[1] : new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
            return !this.changed && direction == Direction.DOWN && !this.state.getValue(WORKING) && itemStack.is(TagRegistry.BRIQUETTES);
        }

        @Override
        public void setChanged() {
            if (!(this.level.getBlockEntity(this.pos) instanceof BriquetteGeneratorBlockEntity blockEntity))
                return;

            if (this.changed)
                return;

            BlockState blockState = this.state.setValue(BRIQUETTE_TYPE, BriquetteType.EMPTY);
            blockEntity.setBriquett(ItemStack.EMPTY);
            this.level.setBlock(pos, blockState, 3);
            SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BG_BRIQUETTE_CHANGE, this.pos, this.level);
            this.changed = true;
        }
    }
}
