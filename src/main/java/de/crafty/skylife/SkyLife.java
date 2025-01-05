package de.crafty.skylife;

import de.crafty.lifecompat.api.fluid.BucketCompatibility;
import de.crafty.lifecompat.api.event.EventManager;
import de.crafty.lifecompat.api.fluid.FluidCompatibility;
import de.crafty.lifecompat.events.BaseEvents;
import de.crafty.skylife.advancements.SkyLifeCriteriaTriggers;
import de.crafty.skylife.command.SkyLifeCommand;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.events.listener.*;
import de.crafty.skylife.registry.*;
import de.crafty.skylife.network.SkyLifeNetworkManager;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.structure.resource_island.ResourceIslandStructure;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyLife implements ModInitializer {

    public static int ISLAND_COUNT = 1;

    public static final String MODID = "skylife";
    public static final Logger LOGGER = LoggerFactory.getLogger("Skylife");

    public static final ResourceLocation JEI_RECIPE_GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/skylife_gui.png");

    private static SkyLife instance;

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("Hello Minecraft!");

        ItemRegistry.load();
        BlockRegistry.load();
        ChunkGenRegistry.perform();
        //WorldGen
        DimensionRegistry.perform();
        DimensionRegistry.MultiNoiseRegistry.perform();
        DimensionRegistry.LevelStem.perform();
        DimensionRegistry.BiomeNoise.perform();
        DimensionRegistry.NoiseSettings.perform();
        DimensionRegistry.Level.perform();

        ItemGroupRegistry.perform();
        BlockEntityRegistry.perform();
        EntityRegistry.perform();
        DataComponentTypeRegistry.perform();
        FluidRegistry.perform();
        InventoryRegistry.perform();
        StructureRegistry.perform();
        StructureRegistry.Pieces.perform();
        FeatureRegistry.perform();

        for (ResourceIslandStructure.ResourceType type : ResourceIslandStructure.ResourceType.values()) {
            LOGGER.info("Known Resource Island Type: {}", type.name().toLowerCase());
        }

        SkyLifeCriteriaTriggers.perform();
        ItemGroupRegistry.registerModItems();

        //TODO Fix crash with addBucketToGroup called twice
        BucketCompatibility.addBucketsToGroup(ResourceLocation.withDefaultNamespace("iron"), ItemRegistry.MOLTEN_OBSIDIAN_BUCKET, ItemRegistry.OIL_BUCKET);

        BucketCompatibility.registerBucketGroup(
                ResourceLocation.fromNamespaceAndPath(MODID, "wood"),
                ItemRegistry.WOODEN_BUCKET,
                ItemRegistry.WOODEN_WATER_BUCKET,
                ItemRegistry.WOODEN_POWDER_SNOW_BUCKET
        );

        FluidCompatibility.addCauldronSupport(FluidRegistry.MOLTEN_OBSIDIAN, BlockRegistry.MOLTEN_OBSIDIAN_CAULDRON, SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
        FluidCompatibility.addCauldronSupport(FluidRegistry.OIL, BlockRegistry.OIL_CAULDRON, SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);

        this.loadConfigs();

        SkyLifeNetworkManager.registerPackets();
        SkyLifeNetworkServer.registerServerReceivers();

        EventManager.registerListener(BaseEvents.PLAYER_ENTER_LEVEL, new PlayerEnterLevelListener());
        EventManager.registerListener(BaseEvents.PLAYER_TOGGLE_SNEAK, new PlayerSneakListener());
        EventManager.registerListener(BaseEvents.PLAYER_MOVE, new PlayerMoveListener());
        EventManager.registerListener(BaseEvents.PLAYER_DEATH, new PlayerDeathListener());

        EventManager.registerListener(BaseEvents.BLOCK_CHANGE, new BlockChangeListener());
        EventManager.registerListener(BaseEvents.BLOCK_INTERACT, new BlockInteractListener());
        EventManager.registerListener(BaseEvents.BLOCK_BREAK, new BlockBreakListener());

        EventManager.registerListener(BaseEvents.WORLD_STARTUP, new WorldStartUpListener());

        EventManager.registerListener(BaseEvents.ITEM_TICK, new ItemTickListener());

        EventManager.registerListener(BaseEvents.BLOCK_ENTITY_LOAD, new BlockEntityLoadListener());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SkyLifeCommand.register(dispatcher));


        LootTableEvents.REPLACE.register((registryKey, builder, lootTableSource, wrapperLookup) -> {
            if (Blocks.DIRT.getLootTable().isPresent() && Blocks.DIRT.getLootTable().get().equals(registryKey)) {

                LootPool.Builder lootPoolBuilder = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                                LootItem.lootTableItem(Items.REDSTONE)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                                        .when(LootItemRandomChanceCondition.randomChance(0.25F))
                                        .otherwise(LootItem.lootTableItem(Items.GOLD_INGOT)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                                .when(LootItemRandomChanceCondition.randomChance(0.25F))
                                        )
                                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(wrapperLookup.lookupOrThrow(Registries.ITEM), ItemTags.SHOVELS)))
                        )
                        .add(LootItem.lootTableItem(Items.DIRT)
                                .when(InvertedLootItemCondition.invert(MatchTool.toolMatches(ItemPredicate.Builder.item().of(wrapperLookup.lookupOrThrow(Registries.ITEM), ItemTags.SHOVELS))))
                        );

                return LootTable.lootTable().withPool(lootPoolBuilder).build();
            }

            return builder;
        });
    }

    public void loadConfigs() {
        SkyLifeConfigs.HAMMER.load();
        SkyLifeConfigs.BLOCK_TRANSFORMATION.load();
        SkyLifeConfigs.SAPLING_GROWTH_CONFIG.load();
        SkyLifeConfigs.BLOCK_MELTING.load();
        SkyLifeConfigs.ITEM_MELTING.load();
        SkyLifeConfigs.LEAF_DROP.load();
        SkyLifeConfigs.FLUID_CONVERSION.load();
        SkyLifeConfigs.OIL_PROCESSING.load();
        SkyLifeConfigs.LOOT_GEM.load();
    }

    public static SkyLife getInstance() {
        return instance;
    }

}
