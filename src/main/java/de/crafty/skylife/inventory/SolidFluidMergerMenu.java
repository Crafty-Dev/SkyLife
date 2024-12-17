package de.crafty.skylife.inventory;

import de.crafty.lifecompat.energy.menu.AbstractEnergyContainerMenu;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.registry.InventoryRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolidFluidMergerMenu extends AbstractEnergyContainerMenu {

    private final Level level;
    private final Container container;
    private final ContainerData data;

    public SolidFluidMergerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2), new SimpleContainerData(8), ContainerLevelAccess.NULL);
    }

    public SolidFluidMergerMenu(int containerId, Inventory inventory, Container container, ContainerData dataAccess, ContainerLevelAccess containerLevelAccess) {
        super(InventoryRegistry.SOLID_FLUID_MERGER, containerId);


        this.container = container;
        this.data = dataAccess;

        this.container.startOpen(inventory.player);
        this.level = inventory.player.level();


        this.addSlot(new InputSlot(this.container, 0, 80, 17));
        this.addSlot(new OutputSlot(this.container, 1, 80, 47));


        //Player Inventory
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18 + 16));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inventory, j, 8 + j * 18, 142 + 16));
        }

        this.addDataSlots(this.data);
    }


    @Override
    public int getStoredEnergy() {
        return this.data.get(0);
    }

    @Override
    public int getCapacity() {
        return this.data.get(1);
    }

    public int getProgress(){
        return this.data.get(2);
    }

    public int getTotalMergingTime(){
        return this.data.get(3);
    }

    public Fluid getFluid(){
        return BuiltInRegistries.FLUID.byId(this.data.get(4));
    }

    public int getFluidVolume(){
        return this.data.get(5);
    }

    public int getFluidCapacity(){
        return this.data.get(6);
    }

    public boolean isWorking(){
        return this.data.get(7) > 0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotId) {
        ItemStack oldStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if (!slot.hasItem())
            return oldStack;

        ItemStack slotStack = slot.getItem();
        oldStack = slotStack.copy();

        if (slotId < this.container.getContainerSize()) {
            if(!this.moveItemStackTo(slotStack, this.container.getContainerSize(), this.slots.size(), true))
                return ItemStack.EMPTY;
        }

        if(slotId >= this.container.getContainerSize()){
            if(!this.moveItemStackTo(slotStack, 0, 1, false))
                return ItemStack.EMPTY;
        }

        if(slotStack.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();

        if(slotStack.getCount() == oldStack.getCount())
            return ItemStack.EMPTY;

        slot.onTake(player, slotStack);

        return oldStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }


    static class InputSlot extends Slot {


        public InputSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return SkyLifeConfigs.FLUID_CONVERSION.getConversions().containsKey(itemStack.getItem());
        }
    }


    static class OutputSlot extends Slot {

        public OutputSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return false;
        }
    }
}
