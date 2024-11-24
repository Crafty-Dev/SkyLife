package de.crafty.skylife;

import de.crafty.lifecompat.api.fluid.BucketCompatibility;
import de.crafty.lifecompat.api.event.EventManager;
import de.crafty.lifecompat.api.fluid.FluidCompatibility;
import de.crafty.lifecompat.events.BaseEvents;
import de.crafty.skylife.advancements.SkyLifeCriteriaTriggers;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.events.listener.*;
import de.crafty.skylife.registry.*;
import de.crafty.skylife.network.SkyLifeNetworkManager;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyLife implements ModInitializer {

    public static final String MODID = "skylife";
    public static final Logger LOGGER = LoggerFactory.getLogger("Skylife");

    public static final ResourceLocation JEI_RECIPE_GUI = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/skylife_gui.png");

    private static SkyLife instance;

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("Hello Minecraft!");

        ItemRegistry.perform();
        BlockRegistry.perform();
        ChunkGenRegistry.perform();
        ItemGroupRegistry.perform();
        BlockEntityRegistry.perform();
        EntityRegistry.perform();
        DataComponentTypeRegistry.perform();
        FluidRegistry.perform();
        InventoryRegistry.perform();
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

        SkyLifeConfigs.HAMMER.load();
        SkyLifeConfigs.BLOCK_TRANSFORMATION.load();
        SkyLifeConfigs.SAPLING_GROWTH_CONFIG.load();
        SkyLifeConfigs.BLOCK_MELTING.load();
        SkyLifeConfigs.LEAF_DROP.load();
        SkyLifeConfigs.FLUID_CONVERSION.load();

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
    }


    public static SkyLife getInstance() {
        return instance;
    }

}