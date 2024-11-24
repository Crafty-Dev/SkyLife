package de.crafty.skylife;

import de.crafty.skylife.blockentities.renderer.*;
import de.crafty.skylife.blockentities.renderer.machines.BlockBreakerRenderer;
import de.crafty.skylife.blockentities.renderer.machines.BriquetteGeneratorRenderer;
import de.crafty.skylife.blockentities.renderer.machines.EnergyStorageRenderer;
import de.crafty.skylife.blockentities.renderer.machines.SolarPanelRenderer;
import de.crafty.skylife.client.model.entity.ResourceSheepEntityFurModel;
import de.crafty.skylife.client.model.entity.ResourceSheepEntityModel;
import de.crafty.skylife.client.renderer.entity.ResourceSheepRenderer;
import de.crafty.skylife.inventory.screens.BlockBreakerScreen;
import de.crafty.skylife.inventory.screens.FluxFurnaceScreen;
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
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;

public class SkyLifeClient implements ClientModInitializer {


    private static SkyLifeClient instance;

    private int currentIslandCount = 1;

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

        ItemProperties.register(ItemRegistry.MOB_ORB, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "active"), (itemStack, clientLevel, livingEntity, i) -> {
            return MobOrbItem.readEntityType(itemStack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag()) == null ? 0.0F : 1.0F;
        });

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> tintIndex == 1 && world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) : -1, BlockRegistry.LEAF_PRESS);

        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.DRIED_LEAVES, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.BRIQUETTE_GENERATOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.SOLAR_PANEL, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.FLUX_FURNACE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.LC_VP_STORAGE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.MC_VP_STORAGE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.HC_VP_STORAGE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.BASIC_ENERGY_CABLE, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putFluid(FluidRegistry.MOLTEN_OBSIDIAN, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluid(FluidRegistry.MOLTEN_OBSIDIAN_FLOWING, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluid(FluidRegistry.OIL, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putFluid(FluidRegistry.OIL_FLOWING, RenderType.translucent());

        //Entities
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP, ResourceSheepEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP_FUR, ResourceSheepEntityFurModel::getTexturedModelData);

        //BlockEntities
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.ENERGY_STORAGE_CORE, EnergyStorageRenderer::createCoreLayer);
        EntityModelLayerRegistry.registerModelLayer(EntityRegistry.ModelLayers.BLOCK_BREAKER_CHAIN, BlockBreakerRenderer::createChainLayer);

        this.registerBlockEntityRenderers();
        this.registerEntityRenderers();

        SkyLifeNetworkClient.registerClientReceivers();

        MenuScreens.register(InventoryRegistry.BLOCK_BREAKER, BlockBreakerScreen::new);
        MenuScreens.register(InventoryRegistry.FLUX_FURNACE, FluxFurnaceScreen::new);

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

    }


    public static SkyLifeClient getInstance() {
        return instance;
    }

    public int getCurrentIslandCount() {
        return this.currentIslandCount;
    }

    public void setCurrentIslandCount(int currentIslandCount) {
        this.currentIslandCount = currentIslandCount;
    }
}
