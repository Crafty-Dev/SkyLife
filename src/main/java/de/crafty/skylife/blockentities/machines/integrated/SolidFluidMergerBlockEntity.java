package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.fluid.blockentity.AbstractFluidEnergyConsumerBlockEntity;
import de.crafty.lifecompat.util.FluidUnitConverter;
import de.crafty.skylife.block.machines.integrated.SolidFluidMergerBlock;
import de.crafty.skylife.config.FluidConversionConfig;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.inventory.SolidFluidMergerMenu;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.util.SkyLifeContainerHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SolidFluidMergerBlockEntity extends AbstractFluidEnergyConsumerBlockEntity implements Container, MenuProvider {

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private int progress, totalMergingTime;
    private final List<ItemStack> recipeOutputs = new ArrayList<>();

    private boolean isWorking = false;

    //8 Seconds
    private static final int BASE_TOTAL_MERGING_TIME = 20 * 8;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int dataSlot) {
            return switch (dataSlot) {
                case 0 -> SolidFluidMergerBlockEntity.this.getStoredEnergy();
                case 1 -> SolidFluidMergerBlockEntity.this.getEnergyCapacity();
                case 2 -> SolidFluidMergerBlockEntity.this.progress;
                case 3 -> SolidFluidMergerBlockEntity.this.totalMergingTime;
                case 4 -> BuiltInRegistries.FLUID.getId(SolidFluidMergerBlockEntity.this.getFluid());
                case 5 -> SolidFluidMergerBlockEntity.this.getVolume();
                case 6 -> SolidFluidMergerBlockEntity.this.getFluidCapacity();
                case 7 -> SolidFluidMergerBlockEntity.this.isWorking ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int dataSlot, int value) {
            switch (dataSlot) {
                case 0 -> SolidFluidMergerBlockEntity.this.setStoredEnergy(value);
                case 1 -> SolidFluidMergerBlockEntity.this.setEnergyCapacity(value);
                case 2 -> SolidFluidMergerBlockEntity.this.progress = value;
                case 3 -> SolidFluidMergerBlockEntity.this.totalMergingTime = value;
                case 4 -> SolidFluidMergerBlockEntity.this.setFluid(BuiltInRegistries.FLUID.byId(value));
                case 5 -> SolidFluidMergerBlockEntity.this.setVolume(value);
                case 6 -> SolidFluidMergerBlockEntity.this.setFluidCapacity(value);
                case 7 -> SolidFluidMergerBlockEntity.this.isWorking = value > 0;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public SolidFluidMergerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.SOLID_FLUID_MERGER, blockPos, blockState, BlockRegistry.SOLID_FLUID_MERGER.getCapacity(), FluidUnitConverter.buckets(2.0F));

        this.totalMergingTime = BASE_TOTAL_MERGING_TIME;
    }


    @Override
    public boolean isAccepting(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isConsuming(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return !this.getItem(0).isEmpty()
                && !SkyLifeConfigs.FLUID_CONVERSION.getDropsForItem(this.getItem(0).getItem(), this.getFluid()).isEmpty()
                && this.getVolume() >= FluidUnitConverter.buckets(1.0F);
    }

    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 60;
    }

    @Override
    public int getConsumptionPerTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 40;
    }

    @Override
    public boolean canDrainLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return false;
    }

    @Override
    public boolean canFillLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }


    @Override
    public int fillWithLiquid(ServerLevel level, BlockPos pos, BlockState state, Fluid liquid, int amount) {
        int filled = super.fillWithLiquid(level, pos, state, liquid, amount);

        if(filled > 0)
            this.recalculateRecipe(level, this.getItem(0).getItem());

        return filled;
    }

    @Override
    public int drainLiquidFrom(ServerLevel level, BlockPos pos, BlockState state, Fluid liquid, int amount) {
        int drained = super.drainLiquidFrom(level, pos, state, liquid, amount);

        if(drained > 0)
            this.recalculateRecipe(level, this.getItem(0).getItem());

        return drained;
    }

    @Override
    protected void performAction(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        ItemStack input = this.getItem(0);

        if (input.isEmpty())
            return;

        if (!this.isWorking)
            this.isWorking = true;

        this.progress++;

        if (this.progress >= this.totalMergingTime) {
            this.progress = 0;

            input.shrink(1);
            if (input.isEmpty() || input.getCount() <= 0)
                this.setItem(0, ItemStack.EMPTY);

            this.handleItemsAfterMerged(serverLevel);
            this.drainLiquidFrom(serverLevel, blockPos, blockState, this.getFluid(), FluidUnitConverter.buckets(1.0F));

        }

        this.setChanged();

    }

    //Returns the stack that fits into the inventory and drops the rest
    private void handleItemsAfterMerged(ServerLevel serverLevel) {
        for (ItemStack stack : this.recipeOutputs) {
            if (stack.isEmpty())
                return;

            ItemStack result = stack.copy();

            for (int i = 1; i < this.getContainerSize(); i++) {
                ItemStack slotItem = this.getItem(i);

                if (slotItem.isEmpty()) {
                    this.setItem(i, result.copy());
                    result.setCount(0);
                    break;
                }

                if (slotItem.is(result.getItem()) && slotItem.getCount() < this.getMaxStackSize(slotItem)) {
                    int prevSlotCount = slotItem.getCount();
                    slotItem.setCount(Math.min(slotItem.getCount() + result.getCount(), this.getMaxStackSize(slotItem)));
                    result.setCount(result.getCount() - (this.getMaxStackSize(slotItem) - prevSlotCount));
                }

            }

            if (result.getCount() > 0)
                Block.popResource(serverLevel, this.getBlockPos().above(), result.copy());
        }

    }


    public boolean isWorking() {
        return this.isWorking;
    }

    public int getProgress(){
        return this.progress;
    }

    public int getTotalMergingTime() {
        return this.totalMergingTime;
    }

    public List<ItemStack> getRecipeOutputs() {
        return this.recipeOutputs;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolidFluidMergerBlockEntity blockEntity) {
        if (level.isClientSide())
            return;

        if (state.getValue(SolidFluidMergerBlock.ENERGY) && blockEntity.getStoredEnergy() < blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state))
            level.setBlock(pos, state.setValue(SolidFluidMergerBlock.ENERGY, false), Block.UPDATE_CLIENTS);

        if (!state.getValue(SolidFluidMergerBlock.ENERGY) && blockEntity.getStoredEnergy() >= blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state))
            level.setBlock(pos, state.setValue(SolidFluidMergerBlock.ENERGY, true), Block.UPDATE_CLIENTS);

        if (!blockEntity.isConsuming((ServerLevel) level, pos, state) && blockEntity.isWorking) {
            blockEntity.isWorking = false;
            blockEntity.setChanged();
        }

        blockEntity.energyTick((ServerLevel) level, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putInt("progress", this.progress);
        tag.putInt("totalMergingTime", this.totalMergingTime);
        tag.putBoolean("working", this.isWorking);

        ListTag recipeOutputs = new ListTag();
        this.recipeOutputs.forEach(recipeOutput -> {
            recipeOutputs.add(recipeOutput.saveOptional(provider));
        });

        tag.put("recipeOutputs", recipeOutputs);

        ContainerHelper.saveAllItems(tag, this.items, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.progress = tag.getInt("progress");
        this.totalMergingTime = tag.getInt("totalMergingTime");
        this.isWorking = tag.getBoolean("working");

        this.recipeOutputs.clear();
        ListTag recipeOutputsTag = tag.getList("recipeOutputs", ListTag.TAG_COMPOUND);
        recipeOutputsTag.forEach(recipeOutput -> {
            this.recipeOutputs.add(ItemStack.parseOptional(provider, (CompoundTag) recipeOutput));
        });

        SkyLifeContainerHelper.loadAllItems(tag, this.items, provider);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput dataComponentInput) {
        dataComponentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag compoundTag) {
        compoundTag.remove("Items");
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.items.get(slot);
    }


    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);

        if (!removed.isEmpty())
            this.setChanged();


        return removed;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        System.out.println("Servus Moin");
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        ItemStack prevStack = this.items.set(slot, itemStack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));

        if (slot == 0 && (itemStack.isEmpty() || itemStack.getItem() != prevStack.getItem())) {
            this.progress = 0;
            this.isWorking = false;

            if (this.level != null && this.level instanceof ServerLevel serverLevel && !itemStack.isEmpty())
                this.recalculateRecipe(serverLevel, itemStack.getItem());
        }

        this.setChanged();


        if(this.level instanceof ServerLevel serverLevel){
            serverLevel.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
            System.out.println("Moln");
        }
    }


    private void recalculateRecipe(ServerLevel serverLevel, Item input) {
        List<FluidConversionConfig.FluidDrop> possibleDrops = SkyLifeConfigs.FLUID_CONVERSION.getDropsForItem(input, this.getFluid());
        RandomSource random = serverLevel.getRandom();

        List<ItemStack> drops = new ArrayList<>();

        for (FluidConversionConfig.FluidDrop fluidDrop : possibleDrops) {
            if (random.nextFloat() >= fluidDrop.chance())
                continue;

            int amount = fluidDrop.min();
            for (int i = fluidDrop.min(); i <= fluidDrop.max(); i++) {
                if (random.nextFloat() < fluidDrop.bonusChance())
                    amount++;
            }

            drops.add(new ItemStack(fluidDrop.output(), amount));
        }

        this.recipeOutputs.clear();
        this.recipeOutputs.addAll(drops);
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
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.skylife.solid_fluid_merger");
    }


    @Override
    public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
        return slot != 0;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return slot != 1;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SolidFluidMergerMenu(containerId, inventory, this, this.dataAccess, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
    }
}
