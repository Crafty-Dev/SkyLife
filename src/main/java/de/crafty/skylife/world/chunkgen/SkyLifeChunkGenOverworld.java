package de.crafty.skylife.world.chunkgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.crafty.skylife.SkyLifeClient;
import de.crafty.skylife.SkyLifeServer;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SkyLifeChunkGenOverworld extends AbstractSkyLifeChunkGenerator {

    public static final MapCodec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
                    )
                    .apply(instance, instance.stable(SkyLifeChunkGenOverworld::new))
    );


    public SkyLifeChunkGenOverworld(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);

    }

    @Override
    protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }


    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState noiseConfig, ChunkAccess chunk) {

    }

    @Override
    public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }


    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureAccessor) {
        ChunkPos chunkPos = chunk.getPos();

        int islandCount = world.getLevel().getServer().isDedicatedServer() ? SkyLifeServer.getInstance().getIslandCountOnServers() : SkyLifeClient.getInstance().getCurrentIslandCount();

        double distance = 75.0F;
        double circumference = (islandCount - 1) * distance;
        double radius = circumference / (Math.PI * 2);

        int structureFreeRadius = (int) (radius + 50);

        if (chunkPos.getMinBlockX() > -structureFreeRadius && chunkPos.getMinBlockX() < structureFreeRadius && chunkPos.getMinBlockZ() > -structureFreeRadius && chunkPos.getMinBlockZ() < structureFreeRadius)
            return;

        super.applyBiomeDecoration(world, chunk, structureAccessor);
    }
}
