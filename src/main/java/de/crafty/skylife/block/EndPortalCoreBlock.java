package de.crafty.skylife.block;

import com.mojang.serialization.MapCodec;
import de.crafty.skylife.blockentities.EndPortalCoreBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.DataComponentTypeRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import de.crafty.skylife.item.MobOrbItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EndPortalCoreBlock extends BaseEntityBlock {

    public static final MapCodec<EndPortalCoreBlock> CODEC = simpleCodec(EndPortalCoreBlock::new);

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 12);

    public EndPortalCoreBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(CHARGE, 0).setValue(ACTIVE, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int currentCharge = world.getBlockState(pos).getValue(CHARGE);

        if (stack.is(Items.ENDER_EYE) && currentCharge != 12) {
            stack.consume(1, player);
            world.setBlock(pos, state.setValue(CHARGE, currentCharge + 1), 3);
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, true);
            return InteractionResult.SUCCESS;
        }

        if (!(stack.getItem() instanceof MobOrbItem))
            return InteractionResult.PASS;

        EntityType<?> entity = MobOrbItem.readEntityType(stack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag());
        if (entity != null && entity.equals(EntityType.ENDERMAN) && currentCharge == 12) {
            if (world.getBlockEntity(pos) instanceof EndPortalCoreBlockEntity blockEntity && this.checkValidMultiblock(world, pos, blockEntity.getPortalFrameLocations(pos)) && world.dimension() != Level.END && world.dimension() != Level.NETHER) {
                world.setBlock(pos, state.setValue(ACTIVE, true), 3);
                blockEntity.startAnimation();
                for(Vec3 posVec : blockEntity.getPortalFrameLocations(pos)){
                    world.setBlock(BlockPos.containing(posVec), world.getBlockState(BlockPos.containing(posVec)).setValue(EndPortalFrameBlock.TRANSFORMING, true), 3);
                }
                player.setItemInHand(hand, new ItemStack(ItemRegistry.MOB_ORB));
                world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 0.5F, 0.25F, false);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if(!state.is(newState.getBlock()) && world.getBlockEntity(pos) instanceof EndPortalCoreBlockEntity blockEntity){
            for(Vec3 frameLoc : blockEntity.getPortalFrameLocations(pos)){
                BlockPos framePos = BlockPos.containing(frameLoc);
                if(world.getBlockState(framePos).getBlock() instanceof EndPortalFrameBlock)
                    world.setBlockAndUpdate(framePos, world.getBlockState(framePos).setValue(EndPortalFrameBlock. TRANSFORMING, false));
            }
        }


        super.onRemove(state, world, pos, newState, moved);
    }

    private boolean checkValidMultiblock(Level level, BlockPos centerPos, Vec3[] portalFrames) {

        for (Vec3 frameLocVec : portalFrames) {
            BlockState state = level.getBlockState(BlockPos.containing(frameLocVec));
            if (!state.is(BlockRegistry.END_PORTAL_FRAME) || state.getValue(EndPortalFrameBlock.FILLED) || state.getValue(EndPortalFrameBlock.TRANSFORMING))
                return false;
        }

        for (int xOff = -1; xOff <= 1; xOff++) {
            for (int zOff = -1; zOff <= 1; zOff++) {
                if (xOff == 0 && zOff == 0)
                    continue;

                BlockState state = level.getBlockState(centerPos.offset(xOff, 0, zOff));
                if (state.isSolid())
                    return false;
            }
        }

        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE, ACTIVE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EndPortalCoreBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.END_PORTAL_CORE, EndPortalCoreBlockEntity::tick);
    }
}
