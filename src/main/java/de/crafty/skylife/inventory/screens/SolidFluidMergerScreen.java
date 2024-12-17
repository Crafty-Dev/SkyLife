package de.crafty.skylife.inventory.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.crafty.lifecompat.energy.screen.AbstractEnergyContainerScreen;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.inventory.SolidFluidMergerMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class SolidFluidMergerScreen extends AbstractEnergyContainerScreen<SolidFluidMergerMenu> {

    private static final ResourceLocation SF_MERGER_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/container/solid_fluid_merger.png");

    public SolidFluidMergerScreen(SolidFluidMergerMenu abstractContainerMenu, Inventory inventory, Component component) {
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(SF_MERGER_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (this.getMenu().isWorking())
            guiGraphics.blit(SF_MERGER_LOCATION, x + 20, y + 22, 0, 182, 136, 57);


        float progression = (float) this.getMenu().getProgress() / (float) this.getMenu().getTotalMergingTime();
        if (progression > 0)
            guiGraphics.blit(SF_MERGER_LOCATION, x + 80, y + 36, 176, 0, 16, Math.round(8 * progression));

        this.renderHorizontalEnergyBar(guiGraphics, x, y, partialTicks);
        this.renderLiquid(guiGraphics, x, y, partialTicks);
        this.renderLiquidInjection(guiGraphics, x, y, partialTicks, progression);
    }


    private void renderLiquid(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        Fluid fluid = this.getMenu().getFluid();

        if (fluid == Fluids.EMPTY)
            return;

        float fluidStatus = (float) this.getMenu().getFluidVolume() / (float) this.getMenu().getFluidCapacity();
        TextureAtlasSprite spriteStill = FluidRenderHandlerRegistryImpl.INSTANCE.get(fluid).getFluidSprites(null, null, fluid.defaultFluidState())[0];


        for (int i = 1; i <= 5; i++) {

            int currentSize = (i - 1) * 12;
            int allowedSize = Math.round(fluidStatus * 60);

            int moreAllowed = allowedSize - currentSize;
            int ySize = Math.max(0, Math.min(moreAllowed, 12));

            if (ySize == 0)
                break;

            int yPos = y + 18 + (60 - allowedSize + ((i - 1) * 12));

            this.renderSprite(guiGraphics, spriteStill, x + 8, yPos, 12, ySize, 0, 0, 16, ySize * (16.0F / 12.0F));
            this.renderSprite(guiGraphics, spriteStill, x + 156, yPos, 12, ySize, 0, 0, 16, ySize * (16.0F / 12.0F));

        }

    }

    private void renderLiquidInjection(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, float progression) {
        if (progression == 0)
            return;

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        Fluid fluid = this.getMenu().getFluid();
        if (fluid == Fluids.EMPTY)
            return;

        TextureAtlasSprite sprite = FluidRenderHandlerRegistryImpl.INSTANCE.get(fluid).getFluidSprites(null, null, fluid.defaultFluidState())[0];
        ResourceLocation texture = sprite.contents().name();
        texture = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "textures/" + texture.getPath() + ".png");

        int totalMergingTime = this.getMenu().getTotalMergingTime();
        float progressedTime = totalMergingTime * progression;

        float partOneTime = totalMergingTime / 3.0F;
        float partTwoTime = totalMergingTime / 3.0F;
        float partThreeTime = totalMergingTime / 3.0F;

        for (int i = 0; i < 4; i++) {

            int allowedWidth = Math.round(Math.min(progressedTime, partOneTime) / partOneTime * 42);
            int currentWidth = i * 12;

            int width = Math.max(0, Math.min(allowedWidth - currentWidth, 12));
            if (width == 0)
                break;

            this.renderSprite(guiGraphics, sprite, x + 20 + (i * 12), y + 74, width, 4, 0, 0, width * (16.0F / 12.0F), 4 * (16.0F / 12.0F));
            this.renderSprite(guiGraphics, sprite, x + 114 + 42 - (i * 12) - width, y + 74, width, 4, 16 - width * (16.0F / 12.0F), 0, width * (16.0F / 12.0F), 4 * (16.0F / 12.0F));
        }

        if(progressedTime < partOneTime)
            return;

        for (int i = 0; i < 5; i++) {
            int allowedHeight = Math.round(Math.min(progressedTime - partOneTime, partTwoTime) / partTwoTime * 51);
            int currentHeight = i * 12;

            int height = Math.max(0, Math.min(allowedHeight - currentHeight, 12));
            if(height == 0)
                break;

            this.renderSprite(guiGraphics, sprite, x + 58, y + 23 + 51 - (i * 12) - height, 4, height, 0, 16 - height * (16.0F / 12.0F), 4 * (16.0F / 12.0F), height * (16.0F / 12.0F));
            this.renderSprite(guiGraphics, sprite, x + 114, y + 23 + 51 - (i * 12) - height, 4, height, 0, 16 - height * (16.0F / 12.0F), 4 * (16.0F / 12.0F), height * (16.0F / 12.0F));

        }

        if(progressedTime < partOneTime + partTwoTime)
            return;

        for(int i = 0; i < 2; i++){
            int allowedWidth = Math.round(Math.min(progressedTime - partOneTime - partTwoTime, partThreeTime) / partThreeTime * 17);
            int currentWidth = i * 12;

            int width = Math.max(0, Math.min(allowedWidth - currentWidth, 12));
            if(width == 0)
                break;

            this.renderSprite(guiGraphics, sprite, x + 62 + (i * 12), y + 23, width, 4, 0, 0, width * (16.0F / 12.0F), 4 * (16.0F / 12.0F));
            this.renderSprite(guiGraphics, sprite, x + 97 + 17 - (i * 12) - width, y + 23, width, 4, 16 - width * (16.0F / 12.0F), 0, width * (16.0F / 12.0F), 4 * (16.0F / 12.0F));

        }

    }

    private void renderSprite(GuiGraphics guiGraphics, TextureAtlasSprite sprite, int x, int y, int width, int height, float u0, float v0, float uWidth, float vWidth) {

        float spriteWidthFloat = (sprite.getU1() - sprite.getU0());
        float spriteHeightFloat = (sprite.getV1() - sprite.getV0());

        u0 = u0 / sprite.contents().width() * spriteWidthFloat;
        v0 = v0 / sprite.contents().height() * spriteHeightFloat;

        uWidth = uWidth / sprite.contents().width() * spriteWidthFloat;
        vWidth = vWidth / sprite.contents().height() * spriteHeightFloat;

        guiGraphics.pose().pushPose();
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());

        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, x, y, 0).setUv(sprite.getU0() + u0, sprite.getV0() + v0);
        bufferBuilder.addVertex(matrix4f, x, y + height, 0).setUv(sprite.getU0() + u0, sprite.getV0() + v0 + vWidth);
        bufferBuilder.addVertex(matrix4f, x + width, y + height, 0).setUv(sprite.getU0() + u0 + uWidth, sprite.getV0() + v0 + vWidth);
        bufferBuilder.addVertex(matrix4f, x + width, y, 0).setUv(sprite.getU0() + u0 + uWidth, sprite.getV0() + v0);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        guiGraphics.pose().popPose();
    }
}
