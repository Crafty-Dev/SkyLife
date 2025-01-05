package de.crafty.skylife.world.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.crafty.skylife.registry.BiomeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EndlessSkiesBiomeSource extends BiomeSource {

    public static final MapCodec<EndlessSkiesBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryOps.retrieveElement(BiomeRegistry.CLOUDY_SKY),
                    RegistryOps.retrieveElement(BiomeRegistry.SKY)
            )
            .apply(instance, instance.stable(EndlessSkiesBiomeSource::new)));

    private final List<Holder<Biome>> biomes;

    @SafeVarargs
    private EndlessSkiesBiomeSource(Holder<Biome> ... biomes) {
        this.biomes = Arrays.asList(biomes);
    }

    public static EndlessSkiesBiomeSource create(HolderGetter<Biome> biomeGetter) {
        return new EndlessSkiesBiomeSource(biomeGetter.getOrThrow(BiomeRegistry.CLOUDY_SKY), biomeGetter.getOrThrow(BiomeRegistry.SKY));
    }

    @Override
    protected @NotNull MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.biomes.stream();
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        x = QuartPos.toBlock(x);
        y = QuartPos.toBlock(y);
        z = QuartPos.toBlock(z);


        int chunkX = (SectionPos.blockToSectionCoord(x) * 2 + 1) * 8;
        int chunkZ = (SectionPos.blockToSectionCoord(z) * 2 + 1) * 8;

        double d = sampler.temperature().compute(new DensityFunction.SinglePointContext(chunkX, y, chunkZ));
        if(d >= 0.0D)
            return this.biomes.getFirst();

        return this.biomes.getLast();
    }
}
