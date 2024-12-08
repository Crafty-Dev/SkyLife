package de.crafty.skylife.blockentities.renderer.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.crafty.lifecompat.fluid.blockentity.AbstractFluidContainerBlockEntity;
import de.crafty.skylife.util.RenderUtils;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;

public class FluidStorageRenderer<T extends AbstractFluidContainerBlockEntity> implements BlockEntityRenderer<T> {


    private final BlockRenderDispatcher blockRenderer;
    private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");

    public FluidStorageRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockRenderer = ctx.getBlockRenderDispatcher();
    }


    @Override
    public void render(T fluidContainer, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        if (fluidContainer.getLevel() == null || fluidContainer.getFluid() == Fluids.EMPTY)
            return;

        TextureAtlasSprite spriteStill = FluidRenderHandlerRegistryImpl.INSTANCE.get(fluidContainer.getFluid()).getFluidSprites(fluidContainer.getLevel(), null, fluidContainer.getFluid().defaultFluidState())[0];

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.translucent());

        int color = fluidContainer.getFluid() == Fluids.WATER ? new Color(BiomeColors.getAverageWaterColor(fluidContainer.getLevel(), fluidContainer.getBlockPos())).getRGB() : -1;

        float fillStatus = (float) fluidContainer.getVolume() / (float) fluidContainer.getCapacity();

        poseStack.pushPose();

        //Top
        if (fillStatus < 1.0F) {
            RenderUtils.renderTexturedPlane(
                    poseStack.last(),
                    vertexConsumer,
                    spriteStill,
                    Direction.UP,
                    px(2.0F),
                    px(1.001F) + px(14.0F) * fillStatus,
                    px(2.0F),
                    px(12.0F),
                    px(12.0F),
                    0.0F,
                    0.0F,
                    1.0F,
                    1.0F,
                    color,
                    light);

        }

        //Sides
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                spriteStill,
                Direction.NORTH,
                px(2.0F + 0.001F),
                px(1.001F),
                px(2.0F + 0.001F),
                px(12.0F - 0.001F),
                px(14.0F - 0.001F) * fillStatus,
                0.0F,
                0.0F,
                1.0F,
                fillStatus,
                color,
                light);

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                spriteStill,
                Direction.EAST,
                px(14.0F - 0.001F),
                px(1.001F),
                px(2.0F + 0.001F),
                px(12.0F - 0.001F),
                px(14.0F - 0.001F) * fillStatus,
                0.0F,
                0.0F,
                1.0F,
                fillStatus,
                color,
                light);

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                spriteStill,
                Direction.SOUTH,
                px(2.0F + 0.001F),
                px(1.001F),
                px(14.0F - 0.001F),
                px(12.0F - 0.001F),
                px(14.0F - 0.001F) * fillStatus,
                0.0F,
                0.0F,
                1.0F,
                fillStatus,
                color,
                light);

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                spriteStill,
                Direction.WEST,
                px(2.0F + 0.001F),
                px(1.001F),
                px(2.0F + 0.001F),
                px(12.0F - 0.001F),
                px(14.0F - 0.001F) * fillStatus,
                0.0F,
                0.0F,
                1.0F,
                fillStatus,
                color,
                light);

        poseStack.popPose();
    }

    private float px(float amount) {
        return 1.0F / 16.0F * amount;
    }
}
