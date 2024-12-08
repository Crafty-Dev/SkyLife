package de.crafty.skylife.block;

import de.crafty.skylife.blockentities.LeafPressBlockEntity;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LeafPressBlock extends Block implements EntityBlock {

    public static final List<Block> VALID_LEAVES = List.of(
            Blocks.OAK_LEAVES,
            Blocks.SPRUCE_LEAVES,
            Blocks.BIRCH_LEAVES,
            Blocks.DARK_OAK_LEAVES,
            Blocks.ACACIA_LEAVES,
            Blocks.JUNGLE_LEAVES,
            Blocks.MANGROVE_LEAVES,
            Blocks.AZALEA_LEAVES,
            Blocks.FLOWERING_AZALEA_LEAVES
    );

    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 6);
    public static final IntegerProperty FLUID_LEVEL = IntegerProperty.create("fluid_level", 0, 4);

    private static final VoxelShape PILLAR_0 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
    private static final VoxelShape PILLAR_1 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape PILLAR_2 = Block.box(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
    private static final VoxelShape PILLAR_3 = Block.box(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);

    private static final VoxelShape BASE_0 = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape BASE_1 = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 2.0D, 15.0D);
    private static final VoxelShape BASE_2 = Block.box(2.0D, 1.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    private static final VoxelShape BASE_3 = Block.box(4.0D, 1.0D, 4.0D, 12.0D, 2.0D, 12.0D);


    private static final VoxelShape BASE = Shapes.or(Shapes.join(Shapes.or(BASE_0, BASE_1), BASE_2, BooleanOp.ONLY_FIRST), BASE_3);
    private static final VoxelShape PILLARS = Shapes.or(PILLAR_0, PILLAR_1, PILLAR_2, PILLAR_3);

    private static final VoxelShape[] PRESS_PLATE = Util.make(new VoxelShape[7], voxelShapes -> {
        for (int i = 0; i < 7; i++) {
            voxelShapes[i] = Block.box(0.0D, 14.0D - (i * 2.0D), 0.0D, 16.0D, 16.0D - (i * 2.0D), 16.0D);
        }
    });

    public LeafPressBlock() {
        super(Properties.of().sound(SoundType.WOOD).isRedstoneConductor(Blocks::never).strength(2.0F));
        this.registerDefaultState(this.getStateDefinition().any().setValue(PROGRESS, 0));
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.or(PILLARS, BASE, PRESS_PLATE[state.getValue(PROGRESS)]);

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROGRESS, FLUID_LEVEL);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        int i = state.getValue(PROGRESS);
        BlockEntity be = world.getBlockEntity(pos);

        if (!(be instanceof LeafPressBlockEntity blockEntity))
            return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(player.getUsedItemHand());

        if (i == 0 && blockEntity.getContent().isEmpty()) {

            if (stack.getItem() instanceof BlockItem b && VALID_LEAVES.contains(b.getBlock())) {
                ItemStack content = stack.copy();
                content.setCount(1);
                blockEntity.setContent(content);

                stack.consume(1, player);
                player.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.5F);
                return InteractionResult.SUCCESS;
            }
        }

        if (i == 0 && blockEntity.getContent().is(BlockRegistry.DRIED_LEAVES.asItem())) {
            player.addItem(blockEntity.getContent());
            player.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
            blockEntity.setContent(ItemStack.EMPTY);
            return InteractionResult.SUCCESS;
        }


        if (!(blockEntity.getContent().getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LeavesBlock) && i == 0)
            return InteractionResult.PASS;


        BlockState newState;

        if (state.getValue(PROGRESS) < 6) {
            newState = state.setValue(PROGRESS, i + 1);
            player.playSound(SoundEvents.WOOD_HIT, 1.0F, 1.5F);
        } else {
            newState = state.setValue(PROGRESS, 0);
            player.playSound(SoundEvents.WOOD_HIT, 1.0F, 1.0F);
        }


        if (newState.getValue(PROGRESS) == 6) {
            blockEntity.setContent(new ItemStack(BlockRegistry.DRIED_LEAVES));
            if (newState.getValue(FLUID_LEVEL) < 4) {
                newState = newState.setValue(FLUID_LEVEL, state.getValue(FLUID_LEVEL) + 1);
                player.playSound(SoundEvents.ITEM_PICKUP, 0.5F, 0.5F);
            }
        }

        world.setBlockAndUpdate(pos, newState);

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LeafPressBlockEntity(pos, state);
    }
}
