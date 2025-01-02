package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.block.machines.integrated.BriquetteGeneratorBlock;
import de.crafty.skylife.item.*;
import de.crafty.skylife.util.TickTimeConverter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.Function;

public class ItemRegistry {

    private static final LinkedHashMap<ResourceLocation, Item> ITEM_LIST = new LinkedHashMap<>();
    private static final LinkedList<Item> NO_GROUP = new LinkedList<>();


    public static final Item OAK_LEAF = register("oak_leaf", new Item.Properties().food(Foods.DRIED_KELP));
    public static final Item BIRCH_LEAF = register("birch_leaf", new Item.Properties().food(Foods.DRIED_KELP));
    public static final Item SPRUCE_LEAF = register("spruce_leaf", new Item.Properties().food(Foods.DRIED_KELP));
    public static final Item DARK_OAK_LEAF = register("dark_oak_leaf", new Item.Properties().food(Foods.DRIED_KELP));
    public static final Item JUNGLE_LEAF = register("jungle_leaf", new Item.Properties().food(Foods.DRIED_KELP));
    public static final Item ACACIA_LEAF = register("acacia_leaf", new Item.Properties().food(Foods.DRIED_KELP));
    public static final Item MANGROVE_LEAF = register("mangrove_leaf", new Item.Properties().food(Foods.DRIED_KELP));
    public static final Item CHERRY_LEAF = register("cherry_leaf", new Item.Properties().food(Foods.DRIED_KELP));

    public static final Item WOODEN_BUCKET = register("wooden_bucket", properties -> new BucketItem(Fluids.EMPTY, properties), new Item.Properties().stacksTo(16));
    public static final Item WOODEN_WATER_BUCKET = register("wooden_water_bucket", properties -> new BucketItem(Fluids.WATER, properties), new Item.Properties().stacksTo(1).craftRemainder(WOODEN_BUCKET));
    public static final Item WOODEN_POWDER_SNOW_BUCKET = register("wooden_powder_snow_bucket", properties -> new SolidBucketItem(Blocks.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, properties), new Item.Properties().stacksTo(1));

    public static final Item MOLTEN_OBSIDIAN_BUCKET = register("molten_obsidian_bucket", properties -> new MoltenObsidianBucket(FluidRegistry.MOLTEN_OBSIDIAN, properties), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
    public static final Item OIL_BUCKET = register("oil_bucket", properties -> new OilBucketItem(FluidRegistry.OIL, properties), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));

    public static final Item WOOD_DUST = register("wood_dust");
    public static final Item STONE_PIECE = register("stone_piece");
    public static final Item NETHERRACK_PIECE = register("netherrack_piece");

    public static final Item ROTTEN_MIXTURE = register("rotten_mixture");
    public static final Item BLAZE_ENRICHED_SEEDS = register("blaze_enriched_seeds");

    public static final Item WOODEN_HAMMER = register("wooden_hammer", properties -> new HammerItem(ToolMaterial.WOOD, 6.5F, -3.2F, properties));
    public static final Item STONE_HAMMER = register("stone_hammer", properties -> new HammerItem(ToolMaterial.STONE, 6.5F, -3.2F, properties));
    public static final Item IRON_HAMMER = register("iron_hammer", properties -> new HammerItem(ToolMaterial.IRON, 6.5F, -3.2F, properties));
    public static final Item GOLDEN_HAMMER = register("golden_hammer", properties -> new HammerItem(ToolMaterial.GOLD, 6.5F, -3.2F, properties));
    public static final Item DIAMOND_HAMMER = register("diamond_hammer", properties -> new HammerItem(ToolMaterial.DIAMOND, 6.5F, -3.2F, properties));
    public static final Item NETHERITE_HAMMER = register("netherite_hammer", properties -> new HammerItem(ToolMaterial.NETHERITE, 6.5F, -3.2F, properties));

    //Ore Dust
    public static final Item COAL_ORE_DUST = register("coal_ore_dust");
    public static final Item IRON_ORE_DUST = register("iron_ore_dust");
    public static final Item COPPER_ORE_DUST = register("copper_ore_dust");
    public static final Item GOLD_ORE_DUST = register("gold_ore_dust");
    public static final Item DIAMOND_ORE_DUST = register("diamond_ore_dust");
    public static final Item EMERALD_ORE_DUST = register("emerald_ore_dust");
    public static final Item LAPIS_ORE_DUST = register("lapis_ore_dust");
    public static final Item REDSTONE_ORE_DUST = register("redstone_ore_dust");

    public static final Item NETHERITE_ORE_DUST = register("netherite_ore_dust");
    public static final Item QUARTZ_ORE_DUST = register("quartz_ore_dust");
    public static final Item GLOWSTONE_ORE_DUST = register("glowstone_ore_dust");

    public static final Item END_NETHERITE_ORE_DUST = register("end_netherite_ore_dust");


    //Dust
    public static final Item COAL_DUST = register("coal_dust");
    public static final Item IRON_DUST = register("iron_dust");
    public static final Item COPPER_DUST = register("copper_dust");
    public static final Item GOLD_DUST = register("gold_dust");
    public static final Item DIAMOND_DUST = register("diamond_dust");
    public static final Item EMERALD_DUST = register("emerald_dust");

    public static final Item NETHERITE_DUST = register("netherite_dust");
    public static final Item QUARTZ_DUST = register("quartz_dust");

    public static final Item MOB_ORB = register("mob_orb", MobOrbItem::new, new Item.Properties().stacksTo(1));

    //Resource Wheat
    public static final ResourceWheatItem COAL_ENRICHED_WHEAT = register("coal_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.COAL_SHEEP, 0.5F, 0.25F, properties), new Item.Properties());
    public static final ResourceWheatItem IRON_ENRICHED_WHEAT = register("iron_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.IRON_SHEEP, 0.4F, 0.2F, properties), new Item.Properties());
    public static final ResourceWheatItem COPPER_ENRICHED_WHEAT = register("copper_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.COPPER_SHEEP, 0.4F, 0.2F, properties), new Item.Properties());
    public static final ResourceWheatItem GOLD_ENRICHED_WHEAT = register("gold_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.GOLD_SHEEP, 0.25F, 0.125F, properties), new Item.Properties());
    public static final ResourceWheatItem LAPIS_ENRICHED_WHEAT = register("lapis_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.LAPIS_SHEEP, 0.35F, 0.125F, properties), new Item.Properties());
    public static final ResourceWheatItem REDSTONE_ENRICHED_WHEAT = register("redstone_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.REDSTONE_SHEEP, 0.3F, 0.225F, properties), new Item.Properties());
    public static final ResourceWheatItem DIAMOND_ENRICHED_WHEAT = register("diamond_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.DIAMOND_SHEEP, 0.1F, 0.1F, properties), new Item.Properties());
    public static final ResourceWheatItem EMERALD_ENRICHED_WHEAT = register("emerald_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.EMERALD_SHEEP, 0.15F, 0.125F, properties), new Item.Properties());

    public static final ResourceWheatItem QUARTZ_ENRICHED_WHEAT = register("quartz_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.QUARTZ_SHEEP, 0.125F, 0.15F, ResourceWheatItem.DimensionCriteria.NETHER, properties), new Item.Properties());
    public static final ResourceWheatItem NETHERITE_ENRICHED_WHEAT = register("netherite_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.NETHERITE_SHEEP, 0.05F, 0.05F, ResourceWheatItem.DimensionCriteria.NETHER, properties), new Item.Properties());
    public static final ResourceWheatItem GLOWSTONE_ENRICHED_WHEAT = register("glowstone_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.GLOWSTONE_SHEEP, 0.45F, 0.15F, ResourceWheatItem.DimensionCriteria.NETHER, properties), new Item.Properties());

    public static final ResourceWheatItem NETHERRACK_ENRICHED_WHEAT = register("netherrack_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.NETHERRACK_SHEEP, 0.65F, 0.15F, ResourceWheatItem.DimensionCriteria.NETHER, properties), new Item.Properties());
    public static final ResourceWheatItem COBBLESTONE_ENRICHED_WHEAT = register("cobblestone_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.COBBLESTONE_SHEEP, 0.75F, 0.2F, properties), new Item.Properties());
    public static final ResourceWheatItem DIRT_ENRICHED_WHEAT = register("dirt_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.DIRT_SHEEP, 0.65F, 0.2F, properties), new Item.Properties());

    public static final ResourceWheatItem OIL_ENRICHED_WHEAT = register("oil_enriched_wheat", properties -> new ResourceWheatItem(EntityRegistry.OIL_SHEEP, 0.0F, 0.0F, properties), new Item.Properties());


    public static final Item SPAWNER_SHARD = register("spawner_shard", new Item.Properties().stacksTo(16).rarity(Rarity.EPIC));
    public static final Item ENDER_CORE = register("ender_core", new Item.Properties().stacksTo(4).rarity(Rarity.EPIC));
    public static final Item DRAGON_ARTIFACT = register("dragon_artifact", new Item.Properties().rarity(Rarity.EPIC).stacksTo(1));

    public static final Item WOOD_BRIQUETT = register("wood_briquette");
    public static final Item COAL_BRIQUETT = register("coal_briquette");

    public static final BriquettItem WOOD_BRIQUETTES = register("wood_briquettes", properties -> new BriquettItem(BriquetteGeneratorBlock.BriquetteType.WOOD, TickTimeConverter.minutes(1.5F), properties), new Item.Properties().stacksTo(1));
    public static final BriquettItem COAL_BRIQUETTES = register("coal_briquettes", properties -> new BriquettItem(BriquetteGeneratorBlock.BriquetteType.COAL, TickTimeConverter.minutes(5.0F), properties), new Item.Properties().stacksTo(1));

    public static final Item UPGRADE_MODULE_TEMPLATE = register("upgrade_module_template");
    public static final Item UPGRADE_MODULE = register("upgrade_module", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item COPPER_COIL = register("copper_coil");
    public static final Item ENHANCED_COPPER_COIL = register("enhanced_copper_coil");
    public static final Item INPUT_MODULE = register("input_module");
    public static final Item OUTPUT_MODULE = register("output_module");
    public static final Item IO_MODULE = register("io_module");
    public static final Item VP_RESISTANT_METAL = register("vp_resistant_metal");
    public static final Item PHOTO_ABSORBER = register("photo_absorber");
    public static final Item INTERNAL_MACHINE_TANK = register("internal_machine_tank", new Item.Properties().stacksTo(2));

    public static final Item PLASTIC = register("plastic");
    public static final Item HARDENED_OIL_FRAGMENT = register("hardened_oil_fragment");
    public static final Item MACHINE_KEY = register("machine_key", MachineKeyItem::new, new Item.Properties().stacksTo(1));

    //Update
    public static final Item STURDY_IRON = register("sturdy_iron");
    public static final Item STURDY_DIAMOND = register("sturdy_diamond");


    //Loot
    public static final LootGemItem STONY_LOOT_GEM = register("stony_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem DIRTY_LOOT_GEM = register("dirty_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem SANDY_LOOT_GEM = register("sandy_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem FISHY_LOOT_GEM = register("fishy_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem EVIL_LOOT_GEM = register("evil_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem NETHER_LOOT_GEM = register("nether_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem ANCIENT_LOOT_GEM = register("ancient_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem END_LOOT_GEM = register("end_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem SHULKY_LOOT_GEM = register("shulky_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8));
    public static final LootGemItem TERRIFYING_LOOT_GEM = register("terrifying_loot_gem", LootGemItem::new, new Item.Properties().stacksTo(8).rarity(Rarity.EPIC));

    private static Item register(String id) {
        return register(id, Item::new, new Item.Properties());
    }

    private static Item register(String id, Item.Properties properties) {
        return register(id, Item::new, properties);
    }

    private static Item register(String id, Function<Item.Properties, Item> itemFactory) {
        return register(id, itemFactory, new Item.Properties());
    }

    private static <T extends Item> T register(String id, Function<Item.Properties, T> itemFactory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id));

        T item = itemFactory.apply(properties.setId(key));
        if (item instanceof BlockItem blockItem)
            blockItem.registerBlocks(Item.BY_BLOCK, item);

        Registry.register(BuiltInRegistries.ITEM, key, item);
        ITEM_LIST.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), item);

        return item;
    }

    public static void load() {
    }

    public static Collection<Item> getItemList() {
        return ITEM_LIST.values();
    }
}
