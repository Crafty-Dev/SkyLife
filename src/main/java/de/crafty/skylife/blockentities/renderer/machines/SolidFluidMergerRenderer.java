package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.blockentities.machines.integrated.SolidFluidMergerBlockEntity;
import de.crafty.skylife.registry.ItemRegistry;
import de.crafty.skylife.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class SolidFluidMergerRenderer extends SimpleEnergyBlockRenderer<SolidFluidMergerBlockEntity> {

    private ItemRenderer itemRenderer;

    public SolidFluidMergerRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);

        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(SolidFluidMergerBlockEntity merger, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        if (merger.getLevel() == null)
            return;

        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.isHolding(ItemRegistry.MACHINE_KEY)) {
            for (Direction side : Direction.values()) {
                this.renderIOSideCentered(merger.getBlockState(), side, poseStack, multiBufferSource, light, overlay);
            }
        }


        this.renderFluidTank(merger, poseStack, multiBufferSource, light, overlay);
        this.renderItem(merger, poseStack, multiBufferSource, light, overlay);
        this.renderFluidAnim(merger, poseStack, multiBufferSource, light, overlay);
    }


    private void renderFluidAnim(SolidFluidMergerBlockEntity merger, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {


        if (!merger.isWorking())
            return;

        Fluid fluid = merger.getFluid();
        if (fluid == Fluids.EMPTY)
            return;

        TextureAtlasSprite sprite = FluidRenderHandlerRegistryImpl.INSTANCE.get(fluid).getFluidSprites(null, null, fluid.defaultFluidState())[0];
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.translucent());
        int color = merger.getFluid() == Fluids.WATER ? new Color(BiomeColors.getAverageWaterColor(merger.getLevel(), merger.getBlockPos())).getRGB() : -1;

        float progression = (float) merger.getProgress() / (float) merger.getTotalMergingTime();
        float fluidHeight = px(1.0F + 1.5F * progression);

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                sprite,
                Direction.UP,
                px(1.0F),
                fluidHeight,
                px(1.0F),
                px(14.0F),
                px(14.0F),
                px(1.0F),
                px(1.0F),
                px(14.0F),
                px(14.0F),
                color,
                light
        );

        //Jets

        float jetHeight = px(8.0F) - (fluidHeight - px(1.0F));

        poseStack.pushPose();
        this.renderJetStream(poseStack, vertexConsumer, sprite, color, light, fluidHeight, jetHeight);
        poseStack.translate(px(10.0F), 0.0F, 0.0F);
        this.renderJetStream(poseStack, vertexConsumer, sprite, color, light, fluidHeight, jetHeight);
        poseStack.translate(0.0F, 0.0F, px(10.0F));
        this.renderJetStream(poseStack, vertexConsumer, sprite, color, light, fluidHeight, jetHeight);
        poseStack.translate(px(-10.0F), 0.0F, 0.0F);
        this.renderJetStream(poseStack, vertexConsumer, sprite, color, light, fluidHeight, jetHeight);
        poseStack.popPose();
    }

    private void renderJetStream(PoseStack poseStack, VertexConsumer vertexConsumer, TextureAtlasSprite sprite, int color, int light, float lowerY, float jetHeight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.translate(px(-3.0F), 0.0F, px(1.25F));
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                sprite,
                Direction.NORTH,
                px(2.0F),
                lowerY,
                px(2.0F),
                px(2.0F),
                jetHeight,
                px(0.0F),
                0.0F,
                px(4.0F),
                jetHeight * 2,
                color,
                light
        );
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.translate(px(-1.0F), 0.0F, px(1.25F));
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                sprite,
                Direction.EAST,
                px(2.0F),
                lowerY,
                px(2.0F),
                px(2.0F),
                jetHeight,
                px(0.0F),
                0.0F,
                px(4.0F),
                jetHeight * 2,
                color,
                light
        );
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.translate(px(-3.0F), 0.0F, px(1.25F));
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                sprite,
                Direction.WEST,
                px(2.0F),
                lowerY,
                px(2.0F),
                px(2.0F),
                jetHeight,
                px(0.0F),
                0.0F,
                px(4.0F),
                jetHeight * 2,
                color,
                light
        );
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.translate(px(-3.0F), 0.0F, px(3.25F));
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vertexConsumer,
                sprite,
                Direction.SOUTH,
                px(2.0F),
                lowerY,
                px(2.0F),
                px(2.0F),
                jetHeight,
                px(0.0F),
                0.0F,
                px(4.0F),
                jetHeight * 2,
                color,
                light
        );
        poseStack.popPose();
    }

    private void renderItem(SolidFluidMergerBlockEntity merger, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        if (merger.getItem(0).isEmpty())
            return;

        ItemStack ingredient = merger.getItem(0);
        ItemStack firstResult = merger.getRecipeOutputs().isEmpty() ? ItemStack.EMPTY : merger.getRecipeOutputs().getFirst();

        poseStack.pushPose();
        poseStack.translate(0.5F, px(2.0F), 0.5F - px(1.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        this.itemRenderer.renderStatic(ingredient, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, merger.getLevel(), (int) merger.getBlockPos().asLong());
        poseStack.popPose();
    }

    private void renderFluidTank(SolidFluidMergerBlockEntity merger, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        if (merger.getFluid() == Fluids.EMPTY)
            return;

        TextureAtlasSprite spriteStill = FluidRenderHandlerRegistryImpl.INSTANCE.get(merger.getFluid()).getFluidSprites(merger.getLevel(), null, merger.getFluid().defaultFluidState())[0];
        int color = merger.getFluid() == Fluids.WATER ? new Color(BiomeColors.getAverageWaterColor(merger.getLevel(), merger.getBlockPos())).getRGB() : -1;

        float fillStatus = (float) merger.getVolume() / (float) merger.getFluidCapacity();

        if (fillStatus < 1.0F)
            RenderUtils.renderTexturedPlane(
                    poseStack.last(),
                    multiBufferSource.getBuffer(RenderType.translucent()),
                    spriteStill,
                    Direction.UP,
                    px(1.0F + 0.01F),
                    px(11.01F + (4.0F - 0.02F) * fillStatus),
                    px(1.0F + 0.01F),
                    px(14.0F - 0.02F),
                    px(14.0F - 0.02F),
                    px(1.0F + 0.01F),
                    px(1.0F + 0.01F),
                    px(14.0F - 0.02F),
                    px(14.0F - 0.02F),
                    color,
                    light
            );


        //Sides
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.NORTH,
                px(1.0F + 0.01F),
                px(11.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((4.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((4.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.SOUTH,
                px(1.0F + 0.01F),
                px(11.0F + 0.01F),
                px(15.0F - 0.01F),
                px(14.0F - 0.02F),
                px((4.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((4.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.EAST,
                px(15.0F - 0.01F),
                px(11.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((4.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((4.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.WEST,
                px(1.0F + 0.01F),
                px(11.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((4.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((4.0F - 0.02F) * fillStatus),
                color,
                light
        );
    }

    private float px(float amount) {
        return 1.0F / 16.0F * amount;
    }
}
