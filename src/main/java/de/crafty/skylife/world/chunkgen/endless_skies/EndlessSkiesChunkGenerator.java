package de.crafty.skylife.world.chunkgen.endless_skies;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.crafty.skylife.world.chunkgen.skylife.SkyLifeChunkGenOverworld;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.NotNull;

public class EndlessSkiesChunkGenerator extends NoiseBasedChunkGenerator {

    public static final MapCodec<EndlessSkiesChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(EndlessSkiesChunkGenerator::generatorSettings)
                    )
                    .apply(instance, instance.stable(EndlessSkiesChunkGenerator::new))
    );

    public EndlessSkiesChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> holder) {
        super(biomeSource, holder);
    }

    @Override
    protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }


    @Override
    public void applyBiomeDecoration(WorldGenLevel worldGenLevel, ChunkAccess chunkAccess, StructureManager structureManager) {
        super.applyBiomeDecoration(worldGenLevel, chunkAccess, structureManager);
    }
}
