package de.crafty.skylife.inventory;

import de.crafty.lifecompat.energy.menu.AbstractEnergyContainerMenu;
import de.crafty.skylife.registry.InventoryRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FluxFurnaceMenu extends AbstractEnergyContainerMenu {

    private final Level level;
    private final Container container;
    private final ContainerData dataAccess;
    private final ContainerLevelAccess levelAccess;
    public FluxFurnaceMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(2), new SimpleContainerData(5), ContainerLevelAccess.NULL);
    }

    public FluxFurnaceMenu(int containerId, Inventory inventory, Container container, ContainerData dataAccess, ContainerLevelAccess containerLevelAccess) {
        super(InventoryRegistry.FLUX_FURNACE, containerId);

        //System.out.println((inventory.player.level().isClientSide() ? "Client: " : "Server: " + menuPos));
        this.container = container;
        this.dataAccess = dataAccess;
        this.levelAccess = containerLevelAccess;

        this.container.startOpen(inventory.player);
        this.level = inventory.player.level();

        this.addSlot(new InputSlot(this.container, 0, 48, 35));
        this.addSlot(new OutputSlot(this.container, 1, 108, 35));


        //Player Slots
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18 + 10));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inventory, j, 8 + j * 18, 142 + 10));
        }

        this.addDataSlots(this.dataAccess);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotId) {
        ItemStack oldStack = ItemStack.EMPTY;
        Slot slot = this.getSlot(slotId);

        if (slot == null || !slot.hasItem())
            return oldStack;

        ItemStack slotStack = slot.getItem();
        oldStack = slotStack.copy();

        if(slotId < this.container.getContainerSize()){
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

    public Container getContainer() {
        return this.container;
    }

    @Override
    public int getStoredEnergy(){
        return this.dataAccess.get(0);
    }

    @Override
    public int getCapacity() {
        return this.dataAccess.get(1);
    }

    public int getSmeltingProgress(){
        return this.dataAccess.get(2);
    }

    public int getSmeltingTotalTime(){
        return this.dataAccess.get(3);
    }

    public boolean hasPerformanceMode(){
        return this.dataAccess.get(4) > 0;
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

    private boolean isSmeltingIngredient(ItemStack stack){
        return this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), this.level).isPresent();
    }


    class InputSlot extends Slot {

        public InputSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return FluxFurnaceMenu.this.isSmeltingIngredient(itemStack);
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
