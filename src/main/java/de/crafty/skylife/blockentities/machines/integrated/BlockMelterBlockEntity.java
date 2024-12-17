package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.fluid.blockentity.AbstractFluidEnergyConsumerBlockEntity;
import de.crafty.lifecompat.util.FluidUnitConverter;
import de.crafty.skylife.block.machines.integrated.BlockMelterBlock;
import de.crafty.skylife.blockentities.MeltingBlockEntity;
import de.crafty.skylife.config.BlockMeltingConfig;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;

public class BlockMelterBlockEntity extends AbstractFluidEnergyConsumerBlockEntity implements ContainerSingleItem.BlockContainerSingleItem {

    private ItemStack meltingStack;
    private int meltingProgress, totalMeltingTime;

    public BlockMelterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BLOCK_MELTER, blockPos, blockState, BlockRegistry.BLOCK_MELTER.getCapacity(), FluidUnitConverter.buckets(4.0F));

        this.meltingStack = ItemStack.EMPTY;
    }


    @Override
    public boolean canDrainLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean canFillLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return false;
    }

    @Override
    public boolean isAccepting(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isConsuming(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.canBeMolten(this.getMeltingBlock());
    }

    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 80;
    }

    @Override
    public int getConsumptionPerTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 40;
    }


    @Override
    protected void performAction(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        if (this.getMeltingBlock() == Blocks.AIR)
            return;

        this.meltingProgress++;

        if (this.meltingProgress == this.totalMeltingTime) {
            this.fillWithLiquid(serverLevel, blockPos, blockState, SkyLifeConfigs.BLOCK_MELTING.getMeltingResultForBlock(this.getMeltingBlock()), FluidUnitConverter.buckets(1.0F));
            this.setMeltingStack(ItemStack.EMPTY);
            SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BM_MELTING_FINISHED, blockPos, level);
        }

    }

    public float getMeltingProgress() {
        return this.totalMeltingTime > 0 ? (float) this.meltingProgress / (float) this.totalMeltingTime : 0.0F;
    }

    public ItemStack getMeltingStack() {
        return this.meltingStack;
    }


    public boolean canBeMolten(Block block) {
        if (block == Blocks.AIR)
            return false;

        return SkyLifeConfigs.BLOCK_MELTING.getMeltingResultForBlock(block) != Fluids.EMPTY;
    }

    public Block getMeltingBlock() {
        return this.meltingStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : Blocks.AIR;
    }

    public void updateRecipe() {

        if (SkyLifeConfigs.BLOCK_MELTING.getMeltingResultForBlock(this.getMeltingBlock()) == Fluids.EMPTY) {
            this.meltingProgress = 0;
            this.totalMeltingTime = 0;
            this.setChanged();
            return;
        }

        float highestEfficiency = 1.0F;
        for (BlockMeltingConfig.HeatSource heatSource : SkyLifeConfigs.BLOCK_MELTING.getMeltables().get(this.getMeltingBlock()).heatSources()) {
            if (heatSource.heatEfficiency() > highestEfficiency)
                highestEfficiency = heatSource.heatEfficiency();
        }

        this.totalMeltingTime = (int) (MeltingBlockEntity.BASE_MELTING_TIME / highestEfficiency);
        this.meltingProgress = 0;
        this.setChanged();
    }

    public void setMeltingStack(ItemStack stack) {
        this.meltingStack = stack;

        this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        this.updateRecipe();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.put("meltingStack", this.meltingStack.saveOptional(provider));


        tag.putInt("meltingProgress", this.meltingProgress);
        tag.putInt("totalMeltingTime", this.totalMeltingTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.meltingStack = ItemStack.parseOptional(provider, tag.getCompound("meltingStack"));

        System.out.println("Loaded");
        this.meltingProgress = tag.getInt("meltingProgress");
        this.totalMeltingTime = tag.getInt("totalMeltingTime");
    }


    public static void tick(Level level, BlockPos pos, BlockState state, BlockMelterBlockEntity blockEntity) {
        if (level.isClientSide())
            return;

        if (blockEntity.getStoredEnergy() < blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state) && blockEntity.meltingProgress > 0) {
            blockEntity.meltingProgress--;
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            if(blockEntity.meltingProgress == 0)
                SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BM_EXSTINGUISH, pos, level);

            blockEntity.setChanged();
        }

        if (!blockEntity.meltingStack.isEmpty() && !blockEntity.canBeMolten(blockEntity.getMeltingBlock())) {
            ItemStack prevStack = blockEntity.meltingStack.copy();

            blockEntity.setMeltingStack(ItemStack.EMPTY);
            Block.popResource(level, pos.above(), prevStack);
        }

        if(state.getValue(BlockMelterBlock.ENERGY) && blockEntity.getStoredEnergy() < blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state))
            level.setBlock(pos, state.setValue(BlockMelterBlock.ENERGY, false), Block.UPDATE_CLIENTS);

        if(!state.getValue(BlockMelterBlock.ENERGY) && blockEntity.getStoredEnergy() >= blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state))
            level.setBlock(pos, state.setValue(BlockMelterBlock.ENERGY, true), Block.UPDATE_CLIENTS);

        blockEntity.energyTick((ServerLevel) level, pos, state);
    }


    @Override
    protected void applyImplicitComponents(DataComponentInput dataComponentInput) {
        super.applyImplicitComponents(dataComponentInput);

        this.meltingStack = dataComponentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);

        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.meltingStack)));
    }

    @Override
    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    @Override
    public @NotNull ItemStack getTheItem() {
        return this.meltingStack;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }


    @Override
    public void setTheItem(ItemStack itemStack) {
        this.setMeltingStack(itemStack);
        if(!this.level.isClientSide())
            SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BM_PLACE_ITEM, this.getBlockPos(), this.level);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return this.meltingStack.isEmpty() && this.getVolume() < this.getFluidCapacity();
    }

    @Override
    public boolean canTakeItem(Container container, int i, ItemStack itemStack) {
        return false;
    }
}
