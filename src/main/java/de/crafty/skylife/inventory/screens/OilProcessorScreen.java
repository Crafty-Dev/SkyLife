package de.crafty.skylife.inventory.screens;

import de.crafty.lifecompat.energy.screen.AbstractEnergyContainerScreen;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.blockentities.machines.integrated.OilProcessorBlockEntity;
import de.crafty.skylife.inventory.OilProcessorMenu;
import de.crafty.skylife.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

@Environment(EnvType.CLIENT)
public class OilProcessorScreen extends AbstractEnergyContainerScreen<OilProcessorMenu> {

    private static final ResourceLocation BURNING_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/container/oil_processor_burning.png");
    private static final ResourceLocation PROCESSING_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/container/oil_processor_processing.png");

    //From Minecraft
    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");

    private int animTick = 0;

    public OilProcessorScreen(OilProcessorMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

        this.imageHeight = 182;
        this.inventoryLabelY = this.inventoryLabelY + 16;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = this.imageWidth / 2 - font.width(this.title) / 2;
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        this.animTick++;
        if (this.animTick == 80) {
            this.animTick = 0;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (this.getMenu().getMode() == OilProcessorBlockEntity.Mode.BURNING)
            guiGraphics.blit(RenderType::guiTextured, BURNING_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        else
            guiGraphics.blit(RenderType::guiTextured, PROCESSING_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        this.renderHorizontalEnergyBar(guiGraphics, x, y, partialTicks);

        //Processing
        if (this.getMenu().getMode() == OilProcessorBlockEntity.Mode.PROCESSING) {
            float progress = (float) this.getMenu().getProgress() / (float) this.getMenu().getTotalProcessingTime();
            guiGraphics.blit(RenderType::guiTextured, PROCESSING_LOCATION, x + 65, y + 33, 176, 0, Math.round(34 * progress), 22, 256, 256);
        }

        //Burning
        if(this.getMenu().getMode() == OilProcessorBlockEntity.Mode.BURNING && this.getMenu().getVolume() > 0 && this.getMenu().getStoredEnergy() < this.getMenu().getCapacity()){
            guiGraphics.blitSprite(RenderType::guiTextured, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - Math.round(14 * (this.animTick / 80.0F)), x + 58, y +  43 + 14 - Math.round(14 * (this.animTick / 80.0F)), 14, Math.round(14 * (this.animTick / 80.0F)));
            guiGraphics.blitSprite(RenderType::guiTextured, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - Math.round(14 * (this.animTick / 80.0F)), x + 80, y +  43 + 14 - Math.round(14 * (this.animTick / 80.0F)), 14, Math.round(14 * (this.animTick / 80.0F)));
            guiGraphics.blitSprite(RenderType::guiTextured, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - Math.round(14 * (this.animTick / 80.0F)), x + 102, y +  43 + 14 - Math.round(14 * (this.animTick / 80.0F)), 14, Math.round(14 * (this.animTick / 80.0F)));

        }


        //Fluid rendering

        Fluid fluid = this.getMenu().getFluid();
        if (fluid == Fluids.EMPTY)
            return;

        float fluidStatus = (float) this.getMenu().getVolume() / (float) this.getMenu().getFluidCapacity();
        TextureAtlasSprite spriteStill = FluidRenderHandlerRegistryImpl.INSTANCE.get(fluid).getFluidSprites(null, null, fluid.defaultFluidState())[0];

        float fluidHeight = fluidStatus * 12;

        for (int i = 0; i < 5; i++) {

            int allowedWidth = 60;
            int currentWidth = i * 12;

            int width = Math.max(0, Math.min(allowedWidth - currentWidth, 12));
            if (width == 0)
                break;

            RenderUtils.renderGuiSprite(guiGraphics, spriteStill, x + 58 + i * width, Math.round(y + (this.getMenu().getMode() == OilProcessorBlockEntity.Mode.BURNING ? 28 : 18) + 12 - fluidHeight), width, Math.round(fluidHeight), 0, 0, 16, fluidHeight * 16.0F / 12.0F);
        }

    }
}
