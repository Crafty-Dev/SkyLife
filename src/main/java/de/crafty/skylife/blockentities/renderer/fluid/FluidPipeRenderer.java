package de.crafty.skylife.blockentities.renderer.fluid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.crafty.lifecompat.api.fluid.logistic.pipe.AbstractFluidPipeBlockEntity;
import de.crafty.lifecompat.api.fluid.logistic.pipe.BaseFluidPipeBlock;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.EntityRegistry;
import de.crafty.skylife.util.RenderUtils;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;
import java.util.List;

public class FluidPipeRenderer<T extends AbstractFluidPipeBlockEntity> implements BlockEntityRenderer<T> {

    private static final ResourceLocation ARROWS = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/fluid/fluid_pipe_io_widgets.png");

    protected final ModelPart downArrowModel, upArrowModel, io_model;

    public FluidPipeRenderer(BlockEntityRendererProvider.Context context) {

        this.downArrowModel = context.bakeLayer(EntityRegistry.ModelLayers.FLUID_PIPE_DOWN_ARROW).getChild("main");
        this.upArrowModel = context.bakeLayer(EntityRegistry.ModelLayers.FLUID_PIPE_UP_ARROW).getChild("main");
        this.io_model = context.bakeLayer(EntityRegistry.ModelLayers.FLUID_PIPE_INOUT).getChild("main");

    }

    @Override
    public void render(T pipe, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        if (pipe.getLevel() == null)
            return;

        BaseFluidPipeBlock.ConnectionState downState = BaseFluidPipeBlock.getConnectionStateForNeighbor(pipe.getLevel(), pipe.getBlockPos(), Direction.DOWN);
        BaseFluidPipeBlock.ConnectionState upState = BaseFluidPipeBlock.getConnectionStateForNeighbor(pipe.getLevel(), pipe.getBlockPos(), Direction.UP);

        BaseFluidPipeBlock.ConnectionState northState = BaseFluidPipeBlock.getConnectionStateForNeighbor(pipe.getLevel(), pipe.getBlockPos(), Direction.NORTH);
        BaseFluidPipeBlock.ConnectionState eastState = BaseFluidPipeBlock.getConnectionStateForNeighbor(pipe.getLevel(), pipe.getBlockPos(), Direction.EAST);
        BaseFluidPipeBlock.ConnectionState southState = BaseFluidPipeBlock.getConnectionStateForNeighbor(pipe.getLevel(), pipe.getBlockPos(), Direction.SOUTH);
        BaseFluidPipeBlock.ConnectionState westState = BaseFluidPipeBlock.getConnectionStateForNeighbor(pipe.getLevel(), pipe.getBlockPos(), Direction.WEST);


        //TransferMode rendering ---------
        if (upState == BaseFluidPipeBlock.ConnectionState.ATTACHED)
            this.renderIOIconsUp(this.getModelForTransferMode(pipe.getTransferMode(Direction.UP), false), poseStack, multiBufferSource, light, overlay);

        if (downState == BaseFluidPipeBlock.ConnectionState.ATTACHED)
            this.renderIOIconsDown(this.getModelForTransferMode(pipe.getTransferMode(Direction.DOWN), true), poseStack, multiBufferSource, light, overlay);

        if (eastState == BaseFluidPipeBlock.ConnectionState.ATTACHED)
            this.renderIOIconsEast(pipe, poseStack, multiBufferSource, light, overlay);

        if (westState == BaseFluidPipeBlock.ConnectionState.ATTACHED)
            this.renderIOIconsWest(pipe, poseStack, multiBufferSource, light, overlay);

        if (northState == BaseFluidPipeBlock.ConnectionState.ATTACHED)
            this.renderIOIconsNorth(pipe, poseStack, multiBufferSource, light, overlay);

        if (southState == BaseFluidPipeBlock.ConnectionState.ATTACHED)
            this.renderIOIconsSouth(pipe, poseStack, multiBufferSource, light, overlay);

        if (pipe.getBufferedFluid() == Fluids.EMPTY)
            return;


        TextureAtlasSprite spriteStill = FluidRenderHandlerRegistryImpl.INSTANCE.get(pipe.getBufferedFluid()).getFluidSprites(pipe.getLevel(), null, pipe.getBufferedFluid().defaultFluidState())[0];
        int color = pipe.getBufferedFluid() == Fluids.WATER ? new Color(BiomeColors.getAverageWaterColor(pipe.getLevel(), pipe.getBlockPos())).getRGB() : -1;

        //Fluid Rendering ---------

        this.renderCoreFluid(poseStack, multiBufferSource.getBuffer(RenderType.translucent()), spriteStill, color, light);

        if (downState != BaseFluidPipeBlock.ConnectionState.NONE)
            this.renderFluidDown(poseStack, multiBufferSource.getBuffer(RenderType.translucent()), spriteStill, color, light);

        if (upState != BaseFluidPipeBlock.ConnectionState.NONE)
            this.renderFluidUp(poseStack, multiBufferSource.getBuffer(RenderType.translucent()), spriteStill, color, light);

        if (northState != BaseFluidPipeBlock.ConnectionState.NONE)
            this.renderFluidNorth(poseStack, multiBufferSource.getBuffer(RenderType.translucent()), spriteStill, color, light);

        if (southState != BaseFluidPipeBlock.ConnectionState.NONE)
            this.renderFluidSouth(poseStack, multiBufferSource.getBuffer(RenderType.translucent()), spriteStill, color, light);

        if (eastState != BaseFluidPipeBlock.ConnectionState.NONE)
            this.renderFluidEast(poseStack, multiBufferSource.getBuffer(RenderType.translucent()), spriteStill, color, light);

        if (westState != BaseFluidPipeBlock.ConnectionState.NONE)
            this.renderFluidWest(poseStack, multiBufferSource.getBuffer(RenderType.translucent()), spriteStill, color, light);
    }

    private void renderIOIconsUp(ModelPart model, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        if (model == null)
            return;

        VertexConsumer vC = multiBufferSource.getBuffer(RenderType.entityCutout(ARROWS));

        for (Direction side : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.translate(px(6.0F), px(15.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            model.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }
    }

    private void renderIOIconsDown(ModelPart model, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        if (model == null)
            return;

        VertexConsumer vC = multiBufferSource.getBuffer(RenderType.entityCutout(ARROWS));

        for (Direction side : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.translate(px(6.0F), px(0.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            model.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }
    }

    private void renderIOIconsEast(T pipe, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        ModelPart model = this.getModelForTransferMode(pipe.getTransferMode(Direction.EAST), false);
        ModelPart modelReversed = this.getModelForTransferMode(pipe.getTransferMode(Direction.EAST), true);


        if (pipe.getTransferMode(Direction.EAST) == AbstractFluidPipeBlockEntity.TransferMode.NONE)
            return;

        VertexConsumer vC = multiBufferSource.getBuffer(RenderType.entityCutout(ARROWS));

        for (Direction side : List.of(Direction.SOUTH)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(-1.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            model.render(poseStack, vC, light, overlay);
            poseStack.popPose();

        }

        for (Direction side : List.of(Direction.NORTH, Direction.DOWN, Direction.UP)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(-16.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            modelReversed.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }
    }

    private void renderIOIconsWest(T pipe, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        ModelPart model = this.getModelForTransferMode(pipe.getTransferMode(Direction.WEST), false);
        ModelPart modelReversed = this.getModelForTransferMode(pipe.getTransferMode(Direction.WEST), true);

        AbstractFluidPipeBlockEntity.TransferMode transferMode = pipe.getTransferMode(Direction.WEST);

        if (transferMode == AbstractFluidPipeBlockEntity.TransferMode.NONE)
            return;


        VertexConsumer vC = multiBufferSource.getBuffer(RenderType.entityCutout(ARROWS));

        for (Direction side : List.of(Direction.NORTH, Direction.DOWN, Direction.UP)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(-1.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            model.render(poseStack, vC, light, overlay);
            poseStack.popPose();

        }

        for (Direction side : List.of(Direction.SOUTH)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.translate(px(6.0F), px(-16.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            modelReversed.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }
    }

    private void renderIOIconsNorth(T pipe, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        ModelPart model = this.getModelForTransferMode(pipe.getTransferMode(Direction.NORTH), false);
        ModelPart modelReversed = this.getModelForTransferMode(pipe.getTransferMode(Direction.NORTH), true);


        if (pipe.getTransferMode(Direction.NORTH) == AbstractFluidPipeBlockEntity.TransferMode.NONE)
            return;

        VertexConsumer vC = multiBufferSource.getBuffer(RenderType.entityCutout(ARROWS));

        for (Direction side : List.of(Direction.WEST)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            poseStack.translate(px(6.0F), px(-16.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            modelReversed.render(poseStack, vC, light, overlay);
            poseStack.popPose();

        }

        for (Direction side : List.of(Direction.EAST)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(-1.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            model.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }

        for (Direction side : List.of(Direction.UP)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            //poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(0.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            modelReversed.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }

        for (Direction side : List.of(Direction.DOWN)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            //poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(15.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            model.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }
    }

    private void renderIOIconsSouth(T pipe, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        ModelPart model = this.getModelForTransferMode(pipe.getTransferMode(Direction.SOUTH), false);
        ModelPart modelReversed = this.getModelForTransferMode(pipe.getTransferMode(Direction.SOUTH), true);


        if (pipe.getTransferMode(Direction.SOUTH) == AbstractFluidPipeBlockEntity.TransferMode.NONE)
            return;

        VertexConsumer vC = multiBufferSource.getBuffer(RenderType.entityCutout(ARROWS));

        for (Direction side : List.of(Direction.WEST)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            poseStack.translate(px(6.0F), px(-1.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            model.render(poseStack, vC, light, overlay);
            poseStack.popPose();

        }

        for (Direction side : List.of(Direction.EAST)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(-16.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            modelReversed.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }

        for (Direction side : List.of(Direction.UP)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            //poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(15.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            model.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }

        for (Direction side : List.of(Direction.DOWN)) {
            poseStack.pushPose();
            this.translateByDirection(side, poseStack);
            //poseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0.0F, 0.0F, 1.0F);
            poseStack.translate(px(6.0F), px(0.0F), px(4.0F - 0.001F));
            poseStack.scale(1.0F / 3.0F, 1.0F / 3.0F, 1.0F);
            modelReversed.render(poseStack, vC, light, overlay);
            poseStack.popPose();
        }
    }


    protected void translateByDirection(Direction direction, PoseStack poseStack) {
        if (direction == Direction.EAST) {
            poseStack.mulPose(Axis.YP.rotationDegrees(270));
            poseStack.translate(0.0F, 0.0F, -1.0F);
        }
        if (direction == Direction.SOUTH) {
            poseStack.translate(1.0F, 0.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }
        if (direction == Direction.WEST) {
            poseStack.translate(0.0F, 0.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }
        if (direction == Direction.UP) {
            poseStack.translate(0.0F, 1.0F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
        }
        if (direction == Direction.DOWN) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.translate(0.0F, -1.0F, 0.0F);
        }
    }

    private ModelPart getModelForTransferMode(AbstractFluidPipeBlockEntity.TransferMode mode, boolean reversed) {
        return switch (mode) {
            case EXTRACTING ->
                    reversed ? this.downArrowModel : this.upArrowModel;
            case INSERTING ->
                    reversed ? this.upArrowModel : this.downArrowModel;
            case INOUT -> this.io_model;

            default -> null;
        };
    }

    private void renderCoreFluid(PoseStack poseStack, VertexConsumer vC, TextureAtlasSprite sprite, int color, int light) {
        //Top
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.UP,
                px(6.0F),
                px(10.5F),
                px(6.0F),
                px(4.0F),
                px(4.0F),
                0.1F,
                0.1F,
                0.8F,
                0.8F,
                color,
                light
        );

        //Down
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.DOWN,
                px(6.0F),
                px(5.5F),
                px(6.0F),
                px(4.0F),
                px(4.0F),
                0.1F,
                0.1F,
                0.8F,
                0.8F,
                color,
                light
        );

        //North
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.NORTH,
                px(6.0F),
                px(6.0F),
                px(5.5F),
                px(4.0F),
                px(4.0F),
                0.1F,
                0.1F,
                0.8F,
                0.8F,
                color,
                light
        );

        //East
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.EAST,
                px(10.5F),
                px(6.0F),
                px(6.0F),
                px(4.0F),
                px(4.0F),
                0.1F,
                0.1F,
                0.8F,
                0.8F,
                color,
                light
        );

        //South
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.SOUTH,
                px(6.0F),
                px(6.0F),
                px(10.5F),
                px(4.0F),
                px(4.0F),
                0.1F,
                0.1F,
                0.8F,
                0.8F,
                color,
                light
        );

        //West
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.WEST,
                px(5.5F),
                px(6.0F),
                px(6.0F),
                px(4.0F),
                px(4.0F),
                0.1F,
                0.1F,
                0.8F,
                0.8F,
                color,
                light
        );
    }

    private void renderFluidUp(PoseStack poseStack, VertexConsumer vC, TextureAtlasSprite sprite, int color, int light) {

        //North
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.NORTH,
                px(7.0F),
                px(11.0F),
                px(5.5F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

        //East
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.EAST,
                px(10.5F),
                px(11.0F),
                px(7.0F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

        //South
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.SOUTH,
                px(7.0F),
                px(11.0F),
                px(10.5F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

        //East
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.WEST,
                px(5.5F),
                px(11.0F),
                px(7.0F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );
    }

    private void renderFluidDown(PoseStack poseStack, VertexConsumer vC, TextureAtlasSprite sprite, int color, int light) {

        //North
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.NORTH,
                px(7.0F),
                px(0.0F),
                px(5.5F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

        //East
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.EAST,
                px(10.5F),
                px(0.0F),
                px(7.0F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

        //South
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.SOUTH,
                px(7.0F),
                px(0.0F),
                px(10.5F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

        //East
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.WEST,
                px(5.5F),
                px(0.0F),
                px(7.0F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

    }

    private void renderFluidNorth(PoseStack poseStack, VertexConsumer vC, TextureAtlasSprite sprite, int color, int light) {


        //East
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.EAST,
                px(10.5F),
                px(7.0F),
                px(0.0F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //West
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.WEST,
                px(5.5F),
                px(7.0F),
                px(0.0F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //Top
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.UP,
                px(7.0F),
                px(10.5F),
                px(0.0F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

        //Down
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.DOWN,
                px(7.0F),
                px(5.5F),
                px(0.0F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

    }

    private void renderFluidSouth(PoseStack poseStack, VertexConsumer vC, TextureAtlasSprite sprite, int color, int light) {

        //East
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.EAST,
                px(10.5F),
                px(7.0F),
                px(11.0F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //West
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.WEST,
                px(5.5F),
                px(7.0F),
                px(11.0F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //Top
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.UP,
                px(7.0F),
                px(10.5F),
                px(11.0F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

        //Down
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.DOWN,
                px(7.0F),
                px(5.5F),
                px(11.0F),
                px(2.0F),
                px(5.0F),
                0.3F,
                0.0F,
                0.4F,
                1.0F,
                color,
                light
        );

    }

    private void renderFluidEast(PoseStack poseStack, VertexConsumer vC, TextureAtlasSprite sprite, int color, int light) {

        //Up
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.UP,
                px(11.0F),
                px(10.5F),
                px(7.0F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //Down
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.DOWN,
                px(11.0F),
                px(5.5F),
                px(7.0F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //North
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.NORTH,
                px(11.0F),
                px(7F),
                px(5.5F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //South
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.SOUTH,
                px(11.0F),
                px(7F),
                px(10.5F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

    }

    private void renderFluidWest(PoseStack poseStack, VertexConsumer vC, TextureAtlasSprite sprite, int color, int light) {

        //Up
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.UP,
                px(0.0F),
                px(10.5F),
                px(7.0F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //Down
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.DOWN,
                px(0.0F),
                px(5.5F),
                px(7.0F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //North
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.NORTH,
                px(0.0F),
                px(7F),
                px(5.5F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

        //South
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                vC,
                sprite,
                Direction.SOUTH,
                px(0.0F),
                px(7F),
                px(10.5F),
                px(5.0F),
                px(2.0F),
                0.0F,
                0.3F,
                1.0F,
                0.4F,
                color,
                light
        );

    }

    private float px(float amount) {
        return 1.0F / 16.0F * amount;
    }

    public static LayerDefinition createUpArrowLayer() {
        return withDefinedUV(2, 0);
    }

    public static LayerDefinition createDownArrowLayer() {
        return withDefinedUV(2, 8);
    }

    public static LayerDefinition createIOLayer() {
        return withDefinedUV(2, 4);
    }

    private static LayerDefinition withDefinedUV(int u, int v) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(u, v).addBox(0.0F, 0.0F, 0.0F, 12.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }
}
