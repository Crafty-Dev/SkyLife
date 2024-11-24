package de.crafty.skylife.events.listener;

import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.api.fluid.FluidCompatibility;
import de.crafty.lifecompat.events.entity.EntityRemoveEvent;
import de.crafty.lifecompat.events.item.ItemTickEvent;
import de.crafty.skylife.logic.FluidConversionLogic;
import net.minecraft.world.level.block.AbstractCauldronBlock;


public class ItemTickListener implements EventListener<ItemTickEvent.Callback> {


    @Override
    public void onEventCallback(ItemTickEvent.Callback callback) {

        if(callback.level().getBlockState(callback.itemEntity().blockPosition()).getBlock() instanceof AbstractCauldronBlock cauldronBlock)
            FluidConversionLogic.onFluidConversion(FluidCompatibility.getFluidInCauldron(cauldronBlock), callback.itemEntity(), callback.level());

    }
}
