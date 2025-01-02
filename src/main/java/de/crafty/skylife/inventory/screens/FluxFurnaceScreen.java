package de.crafty.skylife.inventory.screens;

import de.crafty.lifecompat.energy.screen.AbstractEnergyContainerScreen;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.inventory.FluxFurnaceMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class FluxFurnaceScreen extends AbstractEnergyContainerScreen<FluxFurnaceMenu> {

    public static final ResourceLocation FLUX_FURNACE_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/container/flux_furnace.png");

    public FluxFurnaceScreen(FluxFurnaceMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

        this.imageHeight = 176;
        this.inventoryLabelY = this.inventoryLabelY + 10;

    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = this.imageWidth / 2 - font.width(this.title) / 2;
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

        guiGraphics.blit(RenderType::guiTextured, FLUX_FURNACE_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        int smeltingProgress = this.getMenu().getSmeltingProgress();
        int smeltingTotalTime = this.getMenu().getSmeltingTotalTime();

        float progress = (float) smeltingProgress / (float) smeltingTotalTime;

        if (this.getMenu().hasPerformanceMode()) {
            guiGraphics.blit(RenderType::guiTextured, FLUX_FURNACE_LOCATION, x + 7, y + 12, 176, 60, 8, 62, 256, 256);
            guiGraphics.blit(RenderType::guiTextured, FLUX_FURNACE_LOCATION, x + 161, y + 12, 176, 60, 8, 62, 256, 256);

            guiGraphics.blit(RenderType::guiTextured, FLUX_FURNACE_LOCATION, x + 8, y + 13 + (60 - Math.round(60 * progress)), 176, 0, 6, Math.round(60 * progress), 256, 256);
            guiGraphics.blit(RenderType::guiTextured, FLUX_FURNACE_LOCATION, x + 162, y + 13 + (60 - Math.round(60 * progress)), 176, 0, 6, Math.round(60 * progress), 256, 256);
        }

        guiGraphics.blit(RenderType::guiTextured, FLUX_FURNACE_LOCATION, x + 72, y + 35, 182, this.getMenu().hasPerformanceMode() ? 16 : 0, Math.round(22 * progress), 15, 256, 256);

        this.renderHorizontalEnergyBar(guiGraphics, x, y, partialTicks);
    }

}
