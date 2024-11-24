package de.crafty.skylife.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IUpgradableMachine<S extends BlockEntity> {

    List<Item> validUpgradeItems();


    void onUpgrade(Level level, BlockState machine, BlockPos blockPos, ItemStack upgradeStack, S blockEntity);

    @Nullable Item getCurrentUpgrade(BlockState machine);

    Class<S> getMachineBE();

    default boolean canApplyUpgrade(Level level, BlockState machine, BlockPos blockPos, ItemStack upgradeStack) {
        Item currentUpgrade = getCurrentUpgrade(machine);
        Item checkUpgrade = upgradeStack.getItem();

        return validUpgradeItems().contains(checkUpgrade) && validUpgradeItems().indexOf(checkUpgrade) > (currentUpgrade == null ? -1 : validUpgradeItems().indexOf(currentUpgrade));
    }

}
