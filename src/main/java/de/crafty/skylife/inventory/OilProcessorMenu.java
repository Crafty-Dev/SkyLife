package de.crafty.skylife.inventory;

import de.crafty.lifecompat.energy.menu.AbstractEnergyContainerMenu;
import de.crafty.skylife.blockentities.machines.integrated.OilProcessorBlockEntity;
import de.crafty.skylife.registry.InventoryRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OilProcessorMenu extends AbstractEnergyContainerMenu {

    private final Container container;
    private final ContainerData dataAccess;

    public OilProcessorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2), new SimpleContainerData(8), ContainerLevelAccess.NULL);
    }

    public OilProcessorMenu(int containerId, Inventory playerInventory, Container container, ContainerData dataAccess, ContainerLevelAccess levelAccess) {
        super(InventoryRegistry.OIL_PROCESSOR, containerId);

        this.container = container;
        this.dataAccess = dataAccess;

        this.container.startOpen(playerInventory.player);

        this.addSlot(new OilProcessingItemSlot(this.container, 0, 58, 58));
        this.addSlot(new OutputSlot(this.container, 1, 102, 40));


        //Player Slots
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18 + 16));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142 + 16));
        }

        this.addDataSlots(this.dataAccess);
    }

    @Override
    public int getStoredEnergy() {
        return this.dataAccess.get(0);
    }


    @Override
    public int getCapacity() {
        return this.dataAccess.get(1);
    }

    public int getProgress() {
        return this.dataAccess.get(2);
    }

    public int getTotalProcessingTime() {
        return this.dataAccess.get(3);
    }

    public Fluid getFluid() {
        return BuiltInRegistries.FLUID.byId(this.dataAccess.get(4));
    }

    public int getVolume() {
        return this.dataAccess.get(5);
    }

    public int getFluidCapacity() {
        return this.dataAccess.get(6);
    }

    public OilProcessorBlockEntity.Mode getMode() {
        return OilProcessorBlockEntity.Mode.values()[this.dataAccess.get(7)];
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotId) {
        if(this.getMode() == OilProcessorBlockEntity.Mode.BURNING)
            return ItemStack.EMPTY;

        ItemStack oldStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if (!slot.hasItem())
            return oldStack;

        ItemStack slotStack = slot.getItem();
        oldStack = slotStack.copy();

        if (slotId < this.container.getContainerSize()) {
            if (!this.moveItemStackTo(slotStack, this.container.getContainerSize(), this.slots.size(), true))
                return ItemStack.EMPTY;
        }

        if (slotId >= this.container.getContainerSize()) {
            if (!this.moveItemStackTo(slotStack, 0, 1, false))
                return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();

        if (slotStack.getCount() == oldStack.getCount())
            return ItemStack.EMPTY;

        slot.onTake(player, slotStack);

        return oldStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }


    class OilProcessingItemSlot extends Slot {

        public OilProcessingItemSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return true;
        }

        @Override
        public boolean isActive() {
            return OilProcessorMenu.this.getMode() == OilProcessorBlockEntity.Mode.PROCESSING;
        }
    }

    class OutputSlot extends Slot {

        public OutputSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return false;
        }

        @Override
        public boolean isActive() {
            return OilProcessorMenu.this.getMode() == OilProcessorBlockEntity.Mode.PROCESSING;
        }
    }
}
