package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.fluid.block.BaseFluidEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.skylife.blockentities.machines.integrated.BlockMelterBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BlockMelterBlock extends BaseFluidEnergyBlock {

    public static final MapCodec<BlockMelterBlock> CODEC = simpleCodec(BlockMelterBlock::new);

    private static final VoxelShape TANK = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
    private static final VoxelShape HEAT_BASE = Block.box(3.0D, 13.0D, 3.0D, 13.0D, 14.0D, 13.0D);
    private static final VoxelShape HEAT_BARS = Block.box(4.5D, 14.0D, 4.5D, 11.5D, 15.0D, 11.5D);

    public static final BooleanProperty ENERGY = BooleanProperty.create("energy");

    public BlockMelterBlock(Properties properties) {
        super(properties, Type.CONSUMER, EnergyUnitConverter.kiloVP(10.0F));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(IO_BOTTOM, IOMode.INPUT)
                .setValue(ENERGY, false)
        );
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IO_BOTTOM, ENERGY);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.or(TANK, HEAT_BASE, HEAT_BARS);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockMelterBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.BLOCK_MELTER, BlockMelterBlockEntity::tick);
    }


    @Override
    public List<IOMode> validIOModes() {
        return List.of(IOMode.INPUT, IOMode.NONE);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {

        if(level.getBlockEntity(blockPos) instanceof BlockMelterBlockEntity melter && !melter.getMeltingStack().isEmpty() && blockHitResult.getDirection() != Direction.DOWN) {
            if(!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
                return InteractionResult.sidedSuccess(level.isClientSide());

            ItemStack stack = melter.getMeltingStack().copy();
            melter.setMeltingStack(ItemStack.EMPTY);
            player.addItem(stack);
            player.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if(stack.isEmpty())
            return super.useItemOn(stack, blockState, level, blockPos, player, interactionHand, blockHitResult);

        ItemInteractionResult result = super.useItemOn(stack, blockState, level, blockPos, player, interactionHand, blockHitResult);

        if(result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)
            return result;

        if(level.getBlockEntity(blockPos) instanceof BlockMelterBlockEntity melter) {

            //TODO add sounds
            if(stack.getCount() == 1 && !melter.getMeltingStack().isEmpty()){
                ItemStack prevStack = melter.getMeltingStack().copy();
                ItemStack copied = stack.copy();

                melter.setMeltingStack(copied);
                player.setItemInHand(interactionHand, prevStack);
                player.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 0.5F);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }

            if(melter.getMeltingStack().isEmpty()){
                ItemStack copied = stack.copy();
                copied.setCount(1);

                melter.setMeltingStack(copied);
                stack.shrink(1);
                player.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 0.5F);
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }

        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public List<Direction> getFluidCompatableSides() {
        return List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
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
        return fluid == Fluids.LAVA || fluid == FluidRegistry.MOLTEN_OBSIDIAN ? Optional.of(SoundEvents.BUCKET_EMPTY_LAVA) : Optional.of(SoundEvents.BUCKET_EMPTY);
    }


}
