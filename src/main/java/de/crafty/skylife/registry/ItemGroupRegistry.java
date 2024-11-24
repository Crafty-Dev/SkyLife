package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.HashMap;

public class ItemGroupRegistry {

    private static final HashMap<ResourceLocation, CreativeModeTab> GROUPS = new HashMap<>();
    public static final ResourceKey<CreativeModeTab> SKYLIFE = registerItemGroup("skylife", Items.OAK_SAPLING);


    private static ResourceKey<CreativeModeTab> registerItemGroup(String id, Item display){
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id));
        GROUPS.put(
                ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(display))
                        .title(Component.translatable(String.format("itemGroup.%s.%s", SkyLife.MODID, id)))
                        .build()
        );
        return key;
    }

    public static void perform(){
        GROUPS.forEach((identifier, itemGroup) -> Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, identifier, itemGroup));
    }


    public static void registerModItems(){
        ItemGroupEvents.modifyEntriesEvent(ItemGroupRegistry.SKYLIFE).register(entries -> {
            ItemRegistry.getItemList().forEach(entries::accept);
            BlockRegistry.getBlockList().stream().filter(block -> block.asItem() != Items.AIR).forEach(entries::accept);
        });
    }
}
