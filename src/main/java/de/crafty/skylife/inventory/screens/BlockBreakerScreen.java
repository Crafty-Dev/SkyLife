package de.crafty.skylife.inventory.screens;

import de.crafty.lifecompat.energy.screen.AbstractEnergyContainerScreen;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.blockentities.machines.integrated.BlockBreakerBlockEntity;
import de.crafty.skylife.blockentities.machines.integrated.FluxFurnaceBlockEntity;
import de.crafty.skylife.inventory.BlockBreakerMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class BlockBreakerScreen extends AbstractEnergyContainerScreen<BlockBreakerMenu> {

    private static final ResourceLocation BLOCK_BREAKER_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/container/block_breaker.png");

    private int toolAnimTick = 0;


    public BlockBreakerScreen(BlockBreakerMenu abstractContainerMenu, Inventory inventory, Component component) {
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

        this.toolAnimTick++;

        if (this.toolAnimTick >= 100)
            this.toolAnimTick = 0;

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
        guiGraphics.blit(BLOCK_BREAKER_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (!this.getMenu().getSlot(0).hasItem())
            guiGraphics.blit(BLOCK_BREAKER_LOCATION, x + 80, y + 18, 176, (this.toolAnimTick / 20) * 16, 16, 16);


        this.renderHorizontalEnergyBar(guiGraphics, x, y, partialTicks);
    }
}
