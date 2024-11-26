package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class TagRegistry {

    public static final TagKey<Block> MINEABLE_WITH_HAMMER = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "mineable_with_hammer"));


    public static final TagKey<Item> HAMMERS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "hammers"));
    public static final TagKey<Item> RESSOURCE_WHEAT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "ressource_wheat"));
    public static final TagKey<Item> NETHER_WHEAT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "nether_wheat"));
    public static final TagKey<Item> BRIQUETTES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "briquettes"));

    public static final TagKey<ConfiguredFeature<?, ?>> SKYBLOCK_FEATURES = TagKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "skyblock_features"));
    public static final TagKey<StructureSet> SKYBLOCK_STRUCTURES = TagKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "skyblock_structure_sets"));


    public static final TagKey<Biome> HAS_OAK_ISLAND_SPAWN = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "has_oak_island_spawn"));
    public static final TagKey<Biome> HAS_BIRCH_ISLAND_SPAWN = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "has_birch_island_spawn"));
    public static final TagKey<Biome> HAS_SPRUCE_ISLAND_SPAWN = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "has_spruce_island_spawn"));
    public static final TagKey<Biome> HAS_DARK_OAK_ISLAND_SPAWN = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "has_dark_oak_island_spawn"));
    public static final TagKey<Biome> HAS_JUNGLE_ISLAND_SPAWN = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "has_jungle_island_spawn"));
    public static final TagKey<Biome> HAS_ACACIA_ISLAND_SPAWN = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "has_acacia_island_spawn"));
    public static final TagKey<Biome> HAS_MANGROVE_ISLAND_SPAWN = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "has_mangrove_island_spawn"));


}
