package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentRegistry {


    public static final ResourceKey<Enchantment> CLOUD_WALKER = register("cloud_walker");


    private static ResourceKey<Enchantment> register(String id) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id));
    }
}
