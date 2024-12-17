package de.crafty.skylife.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class SkyLifeContainerHelper {


    //Fix because minecraft was dumb
    public static void loadAllItems(CompoundTag compoundTag, NonNullList<ItemStack> nonNullList, HolderLookup.Provider provider) {
        ListTag listTag = compoundTag.getList("Items", 10);


        for (int i = 0; i < nonNullList.size(); i++) {
            CompoundTag slotTag = listTag.getCompound(i);
            if(slotTag.isEmpty()){
                nonNullList.set(i, ItemStack.EMPTY);
                continue;
            }

            int j = slotTag.getByte("Slot") & 255;
            if (j == i) {
                nonNullList.set(j, ItemStack.parse(provider, slotTag).orElse(ItemStack.EMPTY));
            }else
                nonNullList.set(i, ItemStack.EMPTY);
        }
    }

    public static CompoundTag saveAllItems(CompoundTag compoundTag, NonNullList<ItemStack> nonNullList, HolderLookup.Provider provider) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < nonNullList.size(); i++) {
            ItemStack itemStack = nonNullList.get(i);
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.putByte("Slot", (byte) i);
            listTag.add(itemStack.saveOptional(provider));
        }

        compoundTag.put("Items", listTag);


        return compoundTag;
    }
}
