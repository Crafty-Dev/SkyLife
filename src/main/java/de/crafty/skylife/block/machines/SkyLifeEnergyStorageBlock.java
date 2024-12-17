package de.crafty.skylife.block.machines;

import de.crafty.lifecompat.api.energy.IEnergyHolder;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.skylife.blockentities.machines.SkyLifeEnergyStorageBlockEntity;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class SkyLifeEnergyStorageBlock extends BaseEnergyBlock {

    public static final DirectionProperty FACING = BaseEnergyBlock.HORIZONTAL_FACING;


    public SkyLifeEnergyStorageBlock(Properties properties, Tier tier) {
        super(properties, Type.CONTAINER, tier.getCapacity());

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)

                .setValue(IO_LEFT, IOMode.INPUT)
                .setValue(IO_BACK, IOMode.INPUT)
                .setValue(IO_RIGHT, IOMode.INPUT)
                .setValue(IO_TOP, IOMode.INPUT)
                .setValue(IO_BOTTOM, IOMode.INPUT)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, IO_LEFT, IO_RIGHT, IO_BACK, IO_TOP, IO_BOTTOM);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        InteractionResult result = super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
        if (result != InteractionResult.PASS)
            return result;

        if(level.getBlockEntity(blockPos) instanceof IEnergyHolder holder && level.isClientSide())
            player.sendSystemMessage(Component.translatable("energy.lifecompat.stored.container").append(": ").withStyle(ChatFormatting.GRAY).append(Component.literal(EnergyUnitConverter.format(holder.getStoredEnergy())).withStyle(ChatFormatting.DARK_PURPLE)));

        return InteractionResult.sidedSuccess(level.isClientSide());
    }


    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {

        if(!(level.getBlockEntity(blockPos) instanceof IEnergyHolder holder) || holder.getStoredEnergy() == 0)
            return;

        double x = blockPos.getX() + 0.5F;
        double y = blockPos.getY() + 0.175F;
        double z = blockPos.getZ() + 0.5F;

        for(int i = 0; i < 2 + randomSource.nextInt(3); ++i) {
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, x - 0.15D + 0.3D * randomSource.nextFloat(), y, z - 0.15D + 0.3D * randomSource.nextFloat(), 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return itemStack.is(ItemRegistry.MACHINE_KEY) && this.tryChangeIO(level, blockPos, blockState, player, blockHitResult.getDirection()) ? ItemInteractionResult.sidedSuccess(level.isClientSide()) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public enum Tier {
        LOW(EnergyUnitConverter.kiloVP(100), 40),
        MEDIUM(EnergyUnitConverter.kiloVP(400), 320),
        HIGH(EnergyUnitConverter.kiloVP(1600), 1280);

        final int capacity, maxIO;

        Tier(int capacity, int maxIO) {
            this.capacity = capacity;
            this.maxIO = maxIO;
        }

        public int getCapacity() {
            return this.capacity;
        }

        public int getMaxIO() {
            return this.maxIO;
        }


        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

}
