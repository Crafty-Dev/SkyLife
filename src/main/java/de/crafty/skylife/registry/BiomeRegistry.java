package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class BiomeRegistry {


    public static final ResourceKey<Biome> CLOUDY_SKY = register("cloudy_sky");
    public static final ResourceKey<Biome> SKY = register("sky");

    private static ResourceKey<Biome> register(String id) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id));
    }

}
