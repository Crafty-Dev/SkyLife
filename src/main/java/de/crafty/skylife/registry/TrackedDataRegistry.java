package de.crafty.skylife.registry;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.ItemStack;

public class TrackedDataRegistry {

    public static final EntityDataAccessor<ItemStack> LAST_FOOD = SynchedEntityData.defineId(AgeableMob.class, EntityDataSerializers.ITEM_STACK);

}
