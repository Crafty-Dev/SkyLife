package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.world.biome.EndlessSkiesBiomeSource;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class DimensionRegistry {

    public static final ResourceKey<DimensionType> ENDLESS_SKIES = register("endless_skies");

    private static ResourceKey<DimensionType> register(String id) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id));
    }

    public static void perform() {

    }


    public static class NoiseSettings {

        public static final ResourceKey<NoiseGeneratorSettings> ENDLESS_SKIES = ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "endless_skies"));

        public static void perform() {

        }

    }

    public static class LevelStem {

        public static final ResourceKey<net.minecraft.world.level.dimension.LevelStem> ENDLESS_SKIES = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "endless_skies"));


        public static void perform() {

        }
    }

    public static class Level {

        public static final ResourceKey<net.minecraft.world.level.Level> ENDLESS_SKIES = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "endless_skies"));


        public static void perform() {

        }
    }


    public static class BiomeNoise {

        public static final ResourceKey<MultiNoiseBiomeSourceParameterList> ENDLESS_SKIES = register("endless_skies");

        private static ResourceKey<MultiNoiseBiomeSourceParameterList> register(String id) {
            return ResourceKey.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id));
        }

        public static void perform() {

        }

    }


    public static class MultiNoiseRegistry {


        public static void perform() {
            Registry.register(BuiltInRegistries.BIOME_SOURCE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "endless_skies"), EndlessSkiesBiomeSource.CODEC);
        }

    }
}
