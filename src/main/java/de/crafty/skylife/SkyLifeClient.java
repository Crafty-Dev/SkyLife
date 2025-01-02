package de.crafty.skylife;

import de.crafty.skylife.blockentities.renderer.*;
import de.crafty.skylife.blockentities.renderer.fluid.FluidPipeRenderer;
import de.crafty.skylife.blockentities.renderer.fluid.FluidStorageRenderer;
import de.crafty.skylife.blockentities.renderer.machines.*;
import de.crafty.skylife.client.model.entity.ResourceSheepEntityFurModel;
import de.crafty.skylife.client.model.entity.ResourceSheepEntityModel;
import de.crafty.skylife.client.renderer.entity.ResourceSheepRenderer;
import de.crafty.skylife.inventory.screens.BlockBreakerScreen;
import de.crafty.skylife.inventory.screens.FluxFurnaceScreen;
import de.crafty.skylife.inventory.screens.OilProcessorScreen;
import de.crafty.skylife.inventory.screens.SolidFluidMergerScreen;
import de.crafty.skylife.item.conditional.MobFilled;
import de.crafty.skylife.registry.*;
import de.crafty.skylife.item.MobOrbItem;
import de.crafty.skylife.network.SkyLifeNetworkClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;

public class SkyLifeClient implements ClientModInitializer {


    private static SkyLifeClient instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        FluidRenderHandlerRegistry.INSTANCE.register(FluidRegistry.MOLTEN_OBSIDIAN, FluidRegistry.MOLTEN_OBSIDIAN_FLOWING, new SimpleFluidRenderHandler(
                ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block/molten_obsidian_still"),
                ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block/molten_obsidian_flow")
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(FluidRegistry.OIL, FluidRegistry.OIL_FLOWING, new SimpleFluidRenderHandler(
                ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block/oil_still"),
                ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block/oil_flow")
        ));

        ConditionalItemModelProperties.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "mob_filled"), MobFilled.MAP_CODEC);


        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> tintIndex == 1 && world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) : -1, BlockRegistry.LEAF_PRESS);

        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.DRIED_LEAVES, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.BRIQUETTE_GENERATOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.SOLAR_PANEL, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.FLUX_FURNACE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.BLOCK_MELTER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.FLUID_PUMP, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.SOLID_FLUID_MERGER, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.OIL_PROCESSOR, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.LC_VP_STORAGE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.MC_VP_STORAGE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.HC_VP_STORAGE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.BASIC_ENERGY_CABLE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.IMPROVED_ENERGY_CABLE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.ADVANCED_ENERGY_CABLE, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.BASIC_FLUID_STORAGE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.BASIC_FLUID_PIPE, RenderType.cutout());


        BlockRenderLayerMap.INSTANCE.putFluid(FluidRegistry.MOLTEN_OBSIDIAN, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluid(FluidRegistry.MOLTEN_OBSIDIAN_FLOWING, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluid(FluidRegistry.OIL, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluid(FluidRegistry.OIL_FLOWING, RenderType.translucent());

        //Entities
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP, ResourceSheepEntityModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP_FUR, ResourceSheepEntityFurModel::createFurLayer);

        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP_BABY, ResourceSheepEntityModel::createBabyBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP_BABY_FUR, ResourceSheepEntityFurModel::createBabyFurLayer);


        //BlockEntities
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.ENERGY_STORAGE_CORE, EnergyStorageRenderer::createCoreLayer);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.BLOCK_BREAKER_CHAIN, BlockBreakerRenderer::createChainLayer);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.OIL_PROCESSOR_BURNING_INDICATOR, OilProcessorRenderer::createBurningLayer);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.OIL_PROCESSOR_PROCESSING_INDICATOR, OilProcessorRenderer::createProcessingLayer);

        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.FLUID_PIPE_DOWN_ARROW, FluidPipeRenderer::createDownArrowLayer);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.FLUID_PIPE_UP_ARROW, FluidPipeRenderer::createUpArrowLayer);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.FLUID_PIPE_INOUT, FluidPipeRenderer::createIOLayer);


        this.registerBlockEntityRenderers();
        this.registerEntityRenderers();

        SkyLifeNetworkClient.registerClientReceivers();

        MenuScreens.register(InventoryRegistry.BLOCK_BREAKER, BlockBreakerScreen::new);
        MenuScreens.register(InventoryRegistry.FLUX_FURNACE, FluxFurnaceScreen::new);
        MenuScreens.register(InventoryRegistry.SOLID_FLUID_MERGER, SolidFluidMergerScreen::new);
        MenuScreens.register(InventoryRegistry.OIL_PROCESSOR, OilProcessorScreen::new);

    }

    private void registerBlockEntityRenderers(){
        BlockEntityRenderers.register(BlockEntityRegistry.LEAF_PRESS, LeafPressRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.GRAVE_STONE, GraveStoneRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.END_PORTAL_CORE, EndPortalCoreRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.MAGICAL_WORKBENCH, MagicalWorkbenchRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.MELTING_BLOCK, MeltingBlockRenderer::new);

        BlockEntityRenderers.register(BlockEntityRegistry.LC_VP_STORAGE, EnergyStorageRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.MC_VP_STORAGE, EnergyStorageRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.HC_VP_STORAGE, EnergyStorageRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.BRIQUETTE_GENERATOR, BriquetteGeneratorRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.SOLAR_PANEL, SolarPanelRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.BLOCK_BREAKER, BlockBreakerRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.FLUX_FURNACE, FluxFurnaceRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.BLOCK_MELTER, BlockMelterRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.FLUID_PUMP, FluidPumpRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.SOLID_FLUID_MERGER, SolidFluidMergerRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.OIL_PROCESSOR, OilProcessorRenderer::new);

        BlockEntityRenderers.register(BlockEntityRegistry.BASIC_FLUID_STORAGE, FluidStorageRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.BASIC_FLUID_PIPE, FluidPipeRenderer::new);


    }

    private void registerEntityRenderers(){
        EntityRendererRegistry.register(EntityRegistry.COAL_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.IRON_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.COPPER_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.GOLD_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.LAPIS_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.REDSTONE_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.DIAMOND_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.EMERALD_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.QUARTZ_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.NETHERITE_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.GLOWSTONE_SHEEP, ResourceSheepRenderer::new);

        EntityRendererRegistry.register(EntityRegistry.NETHERRACK_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.COBBLESTONE_SHEEP, ResourceSheepRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.DIRT_SHEEP, ResourceSheepRenderer::new);

        EntityRendererRegistry.register(EntityRegistry.OIL_SHEEP, ResourceSheepRenderer::new);

    }


    public static SkyLifeClient getInstance() {
        return instance;
    }

    public void setCurrentIslandCount(int currentIslandCount) {
        SkyLife.ISLAND_COUNT = currentIslandCount;
    }
}
