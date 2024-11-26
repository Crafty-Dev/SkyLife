package de.crafty.skylife.world.chunkgen;

import com.google.common.base.Suppliers;
import de.crafty.skylife.registry.TagRegistry;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractSkyLifeChunkGenerator extends NoiseBasedChunkGenerator {

    private final Supplier<List<FeatureSorter.StepFeatureData>> indexedFeaturesListGetter;

    public AbstractSkyLifeChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);

        this.indexedFeaturesListGetter = Suppliers.memoize(
                () -> FeatureSorter.buildFeaturesPerStep(
                        List.copyOf(biomeSource.possibleBiomes()), biomeEntry -> this.filterFeatures(generationSettingsGetter.apply(biomeEntry).features()), true
                )
        );
    }

    public static boolean isSkyblockStructureSet(Holder.Reference<StructureSet> structureSetReference) {
        return structureSetReference.is(TagRegistry.SKYBLOCK_STRUCTURES);
    }

    public static boolean isSkyblockFeature(Holder<PlacedFeature> feature) {
        return feature.value().feature().is(TagRegistry.SKYBLOCK_FEATURES);
    }

    public List<HolderSet<PlacedFeature>> filterFeatures(List<HolderSet<PlacedFeature>> featureList){
        List<HolderSet<PlacedFeature>> skyblockAvailableFeatures = new ArrayList<>();

        featureList.forEach(registryEntries -> {
            HolderSet<PlacedFeature> list = HolderSet.direct(registryEntries.stream().filter(AbstractSkyLifeChunkGenerator::isSkyblockFeature).toList());
            if(list.size() > 0)
                skyblockAvailableFeatures.add(list);
        });
        return skyblockAvailableFeatures;
    }

    @Override
    public @NotNull ChunkGeneratorStructureState createState(HolderLookup<StructureSet> structureSetRegistry, RandomState noiseConfig, long seed) {

        List<Holder<StructureSet>> list = structureSetRegistry.listElements().filter(AbstractSkyLifeChunkGenerator::isSkyblockStructureSet).collect(Collectors.toUnmodifiableList());
        return ChunkGeneratorStructureState.createForFlat(noiseConfig, seed, this.biomeSource, list.stream());
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureAccessor) {
        ChunkPos chunkPos = chunk.getPos();
        if (!SharedConstants.debugVoidTerrain(chunkPos)) {
            SectionPos chunkSectionPos = SectionPos.of(chunkPos, world.getMinSection());
            BlockPos blockPos = chunkSectionPos.origin();
            Registry<Structure> registry = world.registryAccess().registryOrThrow(Registries.STRUCTURE);
            Map<Integer, List<Structure>> map = registry.stream()
                    .collect(Collectors.groupingBy(structureType -> structureType.step().ordinal()));
            List<FeatureSorter.StepFeatureData> list = this.indexedFeaturesListGetter.get();
            WorldgenRandom chunkRandom = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
            long l = chunkRandom.setDecorationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());
            Set<Holder<Biome>> set = new ObjectArraySet<>();
            ChunkPos.rangeClosed(chunkSectionPos.chunk(), 1).forEach(pos -> {
                ChunkAccess chunkx = world.getChunk(pos.x, pos.z);

                for (LevelChunkSection chunkSection : chunkx.getSections()) {
                    chunkSection.getBiomes().getAll(set::add);
                }
            });
            set.retainAll(this.biomeSource.possibleBiomes());
            int i = list.size();

            try {
                Registry<PlacedFeature> registry2 = world.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
                int j = Math.max(GenerationStep.Decoration.values().length, i);

                for (int k = 0; k < j; k++) {
                    int m = 0;
                    if (structureAccessor.shouldGenerateStructures()) {
                        for (Structure structure : map.getOrDefault(k, Collections.emptyList())) {
                            chunkRandom.setFeatureSeed(l, m, k);
                            Supplier<String> supplier = () -> (String) registry.getResourceKey(structure).map(Object::toString).orElseGet(structure::toString);

                            try {
                                world.setCurrentlyGenerating(supplier);
                                structureAccessor.startsForStructure(chunkSectionPos, structure)
                                        .forEach(start -> start.placeInChunk(world, structureAccessor, this, chunkRandom, getWritableArea(chunk), chunkPos));
                            } catch (Exception var29) {
                                CrashReport crashReport = CrashReport.forThrowable(var29, "Feature placement");
                                crashReport.addCategory("Feature").setDetail("Description", supplier::get);
                                throw new ReportedException(crashReport);
                            }

                            m++;
                        }
                    }

                    if (k < i) {
                        IntSet intSet = new IntArraySet();

                        for (Holder<Biome> registryEntry : set) {
                            List<HolderSet<PlacedFeature>> list3 = this.filterFeatures(this.generationSettingsGetter.apply(registryEntry).features());
                            if (k < list3.size()) {
                                HolderSet<PlacedFeature> registryEntryList = list3.get(k);
                                FeatureSorter.StepFeatureData indexedFeatures = list.get(k);
                                registryEntryList.stream().map(Holder::value).forEach(feature -> intSet.add(indexedFeatures.indexMapping().applyAsInt(feature)));
                            }
                        }

                        int n = intSet.size();
                        int[] is = intSet.toIntArray();
                        Arrays.sort(is);
                        FeatureSorter.StepFeatureData indexedFeatures2 = list.get(k);

                        for (int o = 0; o < n; o++) {
                            int p = is[o];
                            PlacedFeature placedFeature = indexedFeatures2.features().get(p);
                            Supplier<String> supplier2 = () -> (String) registry2.getResourceKey(placedFeature).map(Object::toString).orElseGet(placedFeature::toString);
                            chunkRandom.setFeatureSeed(l, p, k);

                            try {
                                world.setCurrentlyGenerating(supplier2);
                                placedFeature.placeWithBiomeCheck(world, this, chunkRandom, blockPos);
                            } catch (Exception var30) {
                                CrashReport crashReport2 = CrashReport.forThrowable(var30, "Feature placement");
                                crashReport2.addCategory("Feature").setDetail("Description", supplier2::get);
                                throw new ReportedException(crashReport2);
                            }
                        }
                    }
                }

                world.setCurrentlyGenerating(null);
            } catch (Exception var31) {
                CrashReport crashReport3 = CrashReport.forThrowable(var31, "Biome decoration");
                crashReport3.addCategory("Generation").setDetail("CenterX", chunkPos.x).setDetail("CenterZ", chunkPos.z).setDetail("Decoration Seed", l);
                throw new ReportedException(crashReport3);
            }
        }
    }


}
