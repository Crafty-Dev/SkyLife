package de.crafty.skylife.mixin.world.gen;

import de.crafty.skylife.registry.BiomeRegistry;
import de.crafty.skylife.registry.DimensionRegistry;
import de.crafty.skylife.registry.WorldPresetKeys;
import de.crafty.skylife.world.biome.EndlessSkiesBiomeSource;
import de.crafty.skylife.world.chunkgen.endless_skies.EndlessSkiesChunkGenerator;
import de.crafty.skylife.world.chunkgen.skylife.SkyLifeChunkGenEnd;
import de.crafty.skylife.world.chunkgen.skylife.SkyLifeChunkGenNether;
import de.crafty.skylife.world.chunkgen.skylife.SkyLifeChunkGenOverworld;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(WorldPresets.Bootstrap.class)
public abstract class MixinWorldPresets {


    @Shadow @Final private BootstrapContext<WorldPreset> context;

    @Shadow protected abstract LevelStem makeOverworld(ChunkGenerator chunkGenerator);

    @Shadow @Final private HolderGetter<Biome> biomes;

    @Shadow @Final private HolderGetter<NoiseGeneratorSettings> noiseSettings;

    @Shadow @Final private HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterLists;

    @Inject(method = "registerOverworlds", at = @At("TAIL"))
    private void addSkyLifePreset(BiomeSource biomeSource, CallbackInfo ci){

        HolderGetter<DimensionType> dimensionLookup = this.context.lookup(Registries.DIMENSION_TYPE);
        Holder<DimensionType> netherType = dimensionLookup.getOrThrow(BuiltinDimensionTypes.NETHER);
        Holder<DimensionType> endType = dimensionLookup.getOrThrow(BuiltinDimensionTypes.END);
        Holder<DimensionType> endlessSkiesType = dimensionLookup.getOrThrow(DimensionRegistry.ENDLESS_SKIES);

        Holder<NoiseGeneratorSettings> overworldSettings = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
        Holder<NoiseGeneratorSettings> netherSettings = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER);
        Holder<NoiseGeneratorSettings> endSettings = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.END);
        Holder<NoiseGeneratorSettings> endlessSkiesSettings = this.noiseSettings.getOrThrow(DimensionRegistry.NoiseSettings.ENDLESS_SKIES);


        Holder.Reference<MultiNoiseBiomeSourceParameterList> netherBiomeSource = this.multiNoiseBiomeSourceParameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER);

        LevelStem overworldOptions = this.makeOverworld(new SkyLifeChunkGenOverworld(biomeSource, overworldSettings));
        LevelStem netherOptions = new LevelStem(netherType, new SkyLifeChunkGenNether(MultiNoiseBiomeSource.createFromPreset(netherBiomeSource), netherSettings));
        LevelStem endOptions = new LevelStem(endType, new SkyLifeChunkGenEnd(TheEndBiomeSource.create(this.biomes), endSettings));
        LevelStem endlessSkiesOptions = new LevelStem(endlessSkiesType, new EndlessSkiesChunkGenerator(EndlessSkiesBiomeSource.create(this.biomes), endlessSkiesSettings));

        this.context.register(WorldPresetKeys.SKYLIFE, new WorldPreset(Map.of(LevelStem.OVERWORLD, overworldOptions, LevelStem.NETHER, netherOptions, LevelStem.END, endOptions, DimensionRegistry.LevelStem.ENDLESS_SKIES, endlessSkiesOptions)));
    }

}
