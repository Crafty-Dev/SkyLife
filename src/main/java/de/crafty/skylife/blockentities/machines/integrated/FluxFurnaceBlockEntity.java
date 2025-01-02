package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.api.energy.consumer.AbstractEnergyConsumer;
import de.crafty.skylife.block.machines.integrated.FluxFurnaceBlock;
import de.crafty.skylife.inventory.FluxFurnaceMenu;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class FluxFurnaceBlockEntity extends AbstractEnergyConsumer implements Container, MenuProvider {

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private int smeltingProgress, smeltingTotalTime;
    private boolean performanceMode;

    private final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);


    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int dataSlot) {
            return switch (dataSlot) {
                case 0 -> FluxFurnaceBlockEntity.this.getStoredEnergy();
                case 1 -> FluxFurnaceBlockEntity.this.getEnergyCapacity();
                case 2 -> FluxFurnaceBlockEntity.this.smeltingProgress;
                case 3 -> FluxFurnaceBlockEntity.this.smeltingTotalTime;
                case 4 -> FluxFurnaceBlockEntity.this.performanceMode ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int dataSlot, int value) {
            switch (dataSlot) {
                case 0 -> FluxFurnaceBlockEntity.this.setStoredEnergy(value);
                case 1 -> FluxFurnaceBlockEntity.this.setEnergyCapacity(value);
                case 2 -> FluxFurnaceBlockEntity.this.smeltingProgress = value;
                case 3 -> FluxFurnaceBlockEntity.this.smeltingTotalTime = value;
                case 4 -> FluxFurnaceBlockEntity.this.performanceMode = value > 0;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public FluxFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.FLUX_FURNACE, blockPos, blockState, BlockRegistry.FLUX_FURNACE.getCapacity());

    }

    @Override
    public boolean isAccepting(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isConsuming(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return !this.getItem(0).isEmpty() && (this.getItem(1).isEmpty() || (this.getItem(1).getCount() < this.getMaxStackSize(this.getItem(1)) && this.getItem(1).is(this.getRecipeResult().getItem())));
    }

    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 240;
    }

    @Override
    public int getConsumptionPerTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.performanceMode ? 120 : 80;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putInt("smeltingProgress", this.smeltingProgress);
        tag.putInt("smeltingTotalTime", this.smeltingTotalTime);
        tag.putBoolean("performanceMode", this.performanceMode);

        ContainerHelper.saveAllItems(tag, this.items, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        this.smeltingProgress = compoundTag.getInt("smeltingProgress");
        this.smeltingTotalTime = compoundTag.getInt("smeltingTotalTime");
        this.performanceMode = compoundTag.getBoolean("performanceMode");

        ContainerHelper.loadAllItems(compoundTag, this.items, provider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        this.saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    public int getSmeltingProgress() {
        return this.smeltingProgress;
    }

    public int getSmeltingTotalTime() {
        return this.smeltingTotalTime;
    }

    private ItemStack getRecipeResult() {
        SingleRecipeInput input = new SingleRecipeInput(this.getItem(0));
        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = this.quickCheck.getRecipeFor(input, (ServerLevel) this.getLevel());
        return recipe.map(recipeHolder -> recipeHolder.value().assemble(input, this.level.registryAccess())).orElse(ItemStack.EMPTY);
    }

    private int getRecipeSmeltingTime() {
        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = this.quickCheck.getRecipeFor(new SingleRecipeInput(this.getItem(0)), (ServerLevel) this.getLevel());
        return recipe.map(recipeHolder -> recipeHolder.value().cookingTime()).orElse(200);
    }

    private void resetRecipe() {
        this.smeltingProgress = 0;
        this.smeltingTotalTime = this.getRecipeSmeltingTime();
        this.setChanged();
    }

    public void setPerformanceMode(boolean on){
        this.performanceMode = on;
        this.setChanged();
    }


    @Override
    protected void performAction(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        this.smeltingProgress += this.performanceMode ? 4 : 1;
        if (this.smeltingProgress >= this.smeltingTotalTime) {
            ItemStack result = this.getItem(1);
            if (!result.isEmpty())
                result.setCount(result.getCount() + 1);
            else
                result = this.getRecipeResult();

            this.setItem(1, result);
            this.getItem(0).shrink(1);
            this.resetRecipe();
        }
    }


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, FluxFurnaceBlockEntity blockEntity) {
        if (level.isClientSide())
            return;

        if (blockState.getValue(FluxFurnaceBlock.ACTIVE) && blockEntity.getStoredEnergy() < blockEntity.getConsumptionPerTick((ServerLevel) level, blockPos, blockState))
            level.setBlock(blockPos, blockState.setValue(FluxFurnaceBlock.ACTIVE, false), Block.UPDATE_CLIENTS);

        if (!blockState.getValue(FluxFurnaceBlock.ACTIVE) && blockEntity.getStoredEnergy() >= blockEntity.getConsumptionPerTick((ServerLevel) level, blockPos, blockState))
            level.setBlock(blockPos, blockState.setValue(FluxFurnaceBlock.ACTIVE, true), Block.UPDATE_CLIENTS);

        blockEntity.energyTick((ServerLevel) level, blockPos, blockState);
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
    public int getContainerSize() {
        return 2;
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
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        ItemStack prev = this.items.set(slot, itemStack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));
        this.setChanged();

        if (slot == 0 && (itemStack.isEmpty() || !prev.is(itemStack.getItem())))
            this.resetRecipe();
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
    public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
        return slot != 0;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return slot != 1 && Objects.requireNonNull(this.getLevel()).recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT).test(itemStack);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.skylife.flux_furnace");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerid, Inventory inventory, Player player) {
        return new FluxFurnaceMenu(containerid, inventory, this, this.dataAccess, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
    }
}
