package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.fluid.blockentity.AbstractFluidEnergyContainerBlockEntity;
import de.crafty.lifecompat.util.FluidUnitConverter;
import de.crafty.skylife.block.machines.integrated.OilProcessorBlock;
import de.crafty.skylife.config.OilProcessingConfig;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.inventory.OilProcessorMenu;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.FluidRegistry;
import de.crafty.skylife.util.SkyLifeContainerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OilProcessorBlockEntity extends AbstractFluidEnergyContainerBlockEntity implements Container, MenuProvider {


    private Mode processingMode;

    private int progress, totalProcessingTime;
    private Item currentRecipeOutput;
    private int requiredLiquidAmount;
    private int outputCount;

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int dataSlot) {
            return switch (dataSlot) {
                case 0 -> OilProcessorBlockEntity.this.getStoredEnergy();
                case 1 -> OilProcessorBlockEntity.this.getEnergyCapacity();
                case 2 -> OilProcessorBlockEntity.this.getProgress();
                case 3 -> OilProcessorBlockEntity.this.getTotalProcessingTime();
                case 4 -> BuiltInRegistries.FLUID.getId(OilProcessorBlockEntity.this.getFluid());
                case 5 -> OilProcessorBlockEntity.this.getVolume();
                case 6 -> OilProcessorBlockEntity.this.getFluidCapacity();
                case 7 -> OilProcessorBlockEntity.this.processingMode.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int dataSlot, int value) {
            switch (dataSlot) {
                case 0 -> OilProcessorBlockEntity.this.setStoredEnergy(value);
                case 1 -> OilProcessorBlockEntity.this.setEnergyCapacity(value);
                case 2 -> OilProcessorBlockEntity.this.progress = value;
                case 3 -> OilProcessorBlockEntity.this.totalProcessingTime = value;
                case 4 -> OilProcessorBlockEntity.this.setFluid(BuiltInRegistries.FLUID.byId(value));
                case 5 -> OilProcessorBlockEntity.this.setVolume(value);
                case 6 -> OilProcessorBlockEntity.this.setFluidCapacity(value);
                case 7 -> OilProcessorBlockEntity.this.processingMode = Mode.values()[value];
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public OilProcessorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.OIL_PROCESSOR, blockPos, blockState, BlockRegistry.OIL_PROCESSOR.getCapacity(), FluidUnitConverter.buckets(6.0F));

        this.processingMode = Mode.PROCESSING;
        this.currentRecipeOutput = null;
        this.requiredLiquidAmount = 0;
        this.outputCount = 0;
    }

    @Override
    public boolean isAccepting(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.getProcessingMode() == Mode.PROCESSING;
    }

    @Override
    public boolean isConsuming(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.processingMode == Mode.PROCESSING && this.currentRecipeOutput != null;
    }


    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 240;
    }

    @Override
    public int getConsumptionPerTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 120;
    }

    @Override
    public boolean isTransferring(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.processingMode == Mode.BURNING;
    }

    @Override
    public boolean isGenerating(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.processingMode == Mode.BURNING && this.getVolume() > 0 && this.getStoredEnergy() < this.getEnergyCapacity();
    }

    @Override
    public int getMaxOutput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 480;
    }

    @Override
    public int getGenerationPerTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 320;
    }

    @Override
    public boolean canDrainLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean canFillLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    //Only accept oil
    @Override
    public int fillWithLiquid(ServerLevel level, BlockPos pos, BlockState state, Fluid liquid, int amount) {
        if (liquid != FluidRegistry.OIL)
            return 0;

        int transferred = super.fillWithLiquid(level, pos, state, liquid, amount);
        if (transferred > 0)
            this.recalculateRecipe(level);

        return transferred;
    }

    @Override
    public int drainLiquidFrom(ServerLevel level, BlockPos pos, BlockState state, Fluid liquid, int amount) {
        int drained = super.drainLiquidFrom(level, pos, state, liquid, amount);

        if (drained > 0)
            this.recalculateRecipe(level);

        return drained;
    }

    public void setProcessingMode(Mode processingMode) {
        this.processingMode = processingMode;
        level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        this.setChanged();
        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    public Mode getProcessingMode() {
        return this.processingMode;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getTotalProcessingTime() {
        return this.totalProcessingTime;
    }


    private void recalculateRecipe(ServerLevel serverLevel) {
        OilProcessingConfig.ProcessingRecipe processingRecipe = SkyLifeConfigs.OIL_PROCESSING.getProcessingRecipeFor(this.getItem(0));
        if (processingRecipe == null || this.getVolume() < processingRecipe.liquidAmount() || processingRecipe.liquidAmount() <= 0 || (processingRecipe.output() != this.getItem(1).getItem() && !this.getItem(1).isEmpty()) || (!this.getItem(1).isEmpty() && this.getItem(1).getCount() > this.getMaxStackSize(this.getItem(1)) - processingRecipe.outputCount())) {
            this.progress = 0;
            this.totalProcessingTime = 0;
            this.currentRecipeOutput = null;
            this.outputCount = 0;
            this.setChanged();
            return;
        }

        this.progress = 0;
        this.totalProcessingTime = processingRecipe.processingTime();
        this.currentRecipeOutput = processingRecipe.output();
        this.requiredLiquidAmount = processingRecipe.liquidAmount();
        this.outputCount = processingRecipe.outputCount();
        this.setChanged();
    }

    @Override
    protected void performAction(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        this.progress++;

        if (this.progress >= this.totalProcessingTime) {
            this.progress = 0;

            if (this.getItem(1).isEmpty())
                this.setItem(1, new ItemStack(this.currentRecipeOutput));
            else {
                ItemStack prev = this.getItem(1).copy();
                prev.grow(this.outputCount);
                this.setItem(1, prev);
            }

            if (!this.getItem(0).isEmpty()) {
                ItemStack pi = this.getItem(0).copy();
                pi.shrink(1);
                this.setItem(0, pi.getCount() > 0 ? pi : ItemStack.EMPTY);
            }
            this.drainLiquidFrom(serverLevel, blockPos, blockState, this.getFluid(), this.requiredLiquidAmount);
            serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OilProcessorBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        if (state.getValue(OilProcessorBlock.ENERGY) && blockEntity.getStoredEnergy() < blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state))
            level.setBlock(pos, state.setValue(OilProcessorBlock.ENERGY, false), Block.UPDATE_CLIENTS);

        if (!state.getValue(OilProcessorBlock.ENERGY) && blockEntity.getStoredEnergy() >= blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state))
            level.setBlock(pos, state.setValue(OilProcessorBlock.ENERGY, true), Block.UPDATE_CLIENTS);

        blockEntity.energyTick((ServerLevel) level, pos, state);

        if (blockEntity.getProcessingMode() == Mode.BURNING && blockEntity.getVolume() > 0 && blockEntity.getStoredEnergy() < blockEntity.getEnergyCapacity())
            blockEntity.setVolume(blockEntity.getVolume() - 1);


    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putString("processingMode", this.processingMode.name());
        tag.putInt("progress", this.progress);
        tag.putInt("totalProcessingTime", this.totalProcessingTime);
        tag.putString("currentRecipeOutput", this.currentRecipeOutput == null ? "null" : BuiltInRegistries.ITEM.getKey(this.currentRecipeOutput).toString());
        tag.putInt("requiredLiquidAmount", this.requiredLiquidAmount);
        tag.putInt("outputCount", this.outputCount);

        ContainerHelper.saveAllItems(tag, this.items, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.processingMode = Mode.valueOf(tag.getString("processingMode"));
        this.progress = tag.getInt("progress");
        this.totalProcessingTime = tag.getInt("totalProcessingTime");

        this.currentRecipeOutput = "null".equals(tag.getString("currentRecipeOutput")) ? null : BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getString("currentRecipeOutput")));
        this.requiredLiquidAmount = tag.getInt("requiredLiquidAmount");
        this.outputCount = tag.getInt("outputCount");

        SkyLifeContainerHelper.loadAllItems(tag, this.items, provider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput dataComponentInput) {
        dataComponentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("Items");
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    //TODO Pipes flackern wenn tank(oder anderes) geplaced wird

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);

        if (!removed.isEmpty()) {
            this.setChanged();

            if (slot == 1 && this.currentRecipeOutput == null && this.level instanceof ServerLevel serverLevel)
                this.recalculateRecipe(serverLevel);
        }

        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        ItemStack prevStack = this.items.set(slot, itemStack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));


        if (slot == 0 && (!prevStack.is(itemStack.getItem()) || itemStack.isEmpty()) && this.getLevel() instanceof ServerLevel serverLevel)
            this.recalculateRecipe(serverLevel);

        if (slot == 1 && (itemStack.isEmpty() && this.currentRecipeOutput == null) && this.level instanceof ServerLevel serverLevel)
            this.recalculateRecipe(serverLevel);

        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.skylife.oil_processor");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new OilProcessorMenu(containerId, inventory, this, this.dataAccess, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
    }


    @Override
    public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
        return slot != 0;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return slot != 1;
    }

    public enum Mode {
        PROCESSING,
        BURNING
    }
}
