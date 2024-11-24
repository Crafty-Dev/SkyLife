package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.world.chunkgen.SkyLifeChunkGenEnd;
import de.crafty.skylife.world.chunkgen.SkyLifeChunkGenNether;
import de.crafty.skylife.world.chunkgen.SkyLifeChunkGenOverworld;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ChunkGenRegistry {

    public static void perform(){
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "skylife_overworld"), SkyLifeChunkGenOverworld.CODEC);
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "skylife_nether"), SkyLifeChunkGenNether.CODEC);
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "skylife_end"), SkyLifeChunkGenEnd.CODEC);

    }

}
