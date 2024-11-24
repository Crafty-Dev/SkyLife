package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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


}
