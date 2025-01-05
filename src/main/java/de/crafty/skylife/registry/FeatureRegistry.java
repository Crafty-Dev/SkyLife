package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.world.feature.CloudFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FeatureRegistry {

    public static final Feature<NoneFeatureConfiguration> CLOUD = register("cloud", new CloudFeature(NoneFeatureConfiguration.CODEC));

    private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String id, F feature) {
        return Registry.register(BuiltInRegistries.FEATURE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), feature);
    }



    public static void perform(){

    }

}
