package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class WorldPresetKeys {


    public static final ResourceKey<WorldPreset> SKYLIFE = ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "skylife"));

}
