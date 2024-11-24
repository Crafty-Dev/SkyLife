package de.crafty.skylife.inventory;

import de.crafty.lifecompat.energy.menu.AbstractEnergyContainerMenu;
import de.crafty.skylife.registry.InventoryRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.jetbrains.annotations.NotNull;

public class BlockBreakerMenu extends AbstractEnergyContainerMenu {


    private final Container container;
    private final ContainerData dataAccess;

    public BlockBreakerMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(19), new SimpleContainerData(2));
    }

    public BlockBreakerMenu(int i, Inventory inventory, Container container, ContainerData data) {
        super(InventoryRegistry.BLOCK_BREAKER, i);

        this.container = container;
        this.dataAccess = data;

        this.container.startOpen(inventory.player);

        //Tool Slot
        this.addSlot(new ToolSlot(this.container, 0, 80, 18));

        //Output slots
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(this.container, x + y * 9 + 1, 8 + x * 18, 50 + y * 18));
            }
        }

        //Player Inventory Slots
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 100 + j * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inventory, j, 8 + j * 18, 158));
        }

        this.addDataSlots(this.dataAccess);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotId) {
        ItemStack oldStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot == null || !slot.hasItem())
            return oldStack;

        ItemStack slotStack = slot.getItem();
        oldStack = slotStack.copy();

        //Move to player inventory
        if (slotId < this.container.getContainerSize()) {
            if (!this.moveItemStackTo(slotStack, this.container.getContainerSize(), this.slots.size(), true))
                return ItemStack.EMPTY;
        }

        //Tool move logic
        if (slotId >= this.container.getContainerSize()) {
            if (!this.moveItemStackTo(slotStack, 0, 1, false))
                return ItemStack.EMPTY;
        }


        if (slotStack.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();


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


    @Override
    public int getStoredEnergy() {
        return this.dataAccess.get(0);
    }

    @Override
    public int getCapacity() {
        return this.dataAccess.get(1);
    }

    static class ToolSlot extends Slot {


        public ToolSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return !itemStack.isEmpty() && itemStack.getItem() instanceof TieredItem;
        }
    }
}
