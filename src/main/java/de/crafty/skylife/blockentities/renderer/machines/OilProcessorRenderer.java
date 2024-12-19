package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.block.machines.integrated.OilProcessorBlock;
import de.crafty.skylife.blockentities.machines.integrated.OilProcessorBlockEntity;
import de.crafty.skylife.registry.EntityRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import de.crafty.skylife.util.RenderUtils;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;

public class OilProcessorRenderer extends SimpleEnergyBlockRenderer<OilProcessorBlockEntity> {

    private static final ResourceLocation BURNING_INDICATOR = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/oil_processor/burning_indicator.png");
    private static final ResourceLocation PROCESSING_INDICATOR = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/oil_processor/processing_indicator.png");


    private final ModelPart burningIndicatorModel, processingIndicatorModel;

    public OilProcessorRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);

        this.burningIndicatorModel = ctx.bakeLayer(EntityRegistry.ModelLayers.OIL_PROCESSOR_BURNING_INDICATOR).getChild("main");
        this.processingIndicatorModel = ctx.bakeLayer(EntityRegistry.ModelLayers.OIL_PROCESSOR_PROCESSING_INDICATOR).getChild("main");
    }

    @Override
    public void render(OilProcessorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        if(blockEntity.getLevel() == null)
            return;

        LocalPlayer player = Minecraft.getInstance().player;

        if(player != null && player.isHolding(ItemRegistry.MACHINE_KEY)){
            for(Direction side : Direction.values()) {
                this.renderIOSideCentered(blockEntity.getBlockState(), side, poseStack, multiBufferSource, light, overlay);
            }
        }


        poseStack.pushPose();
        this.translateByDirection(blockEntity.getBlockState().getValue(OilProcessorBlock.FACING), poseStack);
        if(blockEntity.getProcessingMode() == OilProcessorBlockEntity.Mode.PROCESSING){
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.translate(px(-10.0F), px(0.75F), px(-6.25F));
            poseStack.scale(0.35F, 0.35F, 0.35F);
            this.processingIndicatorModel.render(poseStack, multiBufferSource.getBuffer(RenderType.entityCutout(PROCESSING_INDICATOR)), light, overlay);
        }else {
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.translate(px(-11.0F), px(-11.625F), px(11.5F));
            poseStack.scale(0.35F, 0.35F, 0.35F);
            this.burningIndicatorModel.render(poseStack, multiBufferSource.getBuffer(RenderType.entityCutout(BURNING_INDICATOR)), light, overlay);
        }

        poseStack.popPose();

        if(blockEntity.getFluid() == Fluids.EMPTY)
            return;

        TextureAtlasSprite spriteStill = FluidRenderHandlerRegistryImpl.INSTANCE.get(blockEntity.getFluid()).getFluidSprites(blockEntity.getLevel(), null, blockEntity.getFluid().defaultFluidState())[0];
        int color = blockEntity.getFluid() == Fluids.WATER ? new Color(BiomeColors.getAverageWaterColor(blockEntity.getLevel(), blockEntity.getBlockPos())).getRGB() : -1;

        float fillStatus = (float) blockEntity.getVolume() / (float) blockEntity.getFluidCapacity();

        this.translateByDirection(blockEntity.getBlockState().getValue(OilProcessorBlock.FACING), poseStack);

        if (fillStatus < 1.0F){
            RenderUtils.renderTexturedPlane(
                    poseStack.last(),
                    multiBufferSource.getBuffer(RenderType.translucent()),
                    spriteStill,
                    Direction.UP,
                    px(1.0F + 0.01F),
                    px(1.01F + (13.0F - 0.02F) * fillStatus),
                    px(1.0F + 0.01F),
                    px(2.0F - 0.02F),
                    px(14.0F - 0.02F),
                    px(1.0F + 0.01F),
                    px(1.0F + 0.01F),
                    px(2.0F - 0.02F),
                    px(14.0F - 0.02F),
                    color,
                    light
            );
        }

        //Sides
        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.NORTH,
                px(1.0F + 0.01F),
                px(1.0F + 0.01F),
                px(1.0F + 0.01F),
                px(2.0F - 0.02F),
                px((13.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(2.0F - 0.02F),
                px((13.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.SOUTH,
                px(1.0F + 0.01F),
                px(1.0F + 0.01F),
                px(15.0F - 0.01F),
                px(2.0F - 0.02F),
                px((13.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(2.0F - 0.02F),
                px((13.0F - 0.02F) * fillStatus),
                color,
                light
        );


        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.EAST,
                px(3.0F - 0.01F),
                px(1.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((13.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((13.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.WEST,
                px(1.0F + 0.01F),
                px(1.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((13.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((13.0F - 0.02F) * fillStatus),
                color,
                light
        );

    }


    private float px(float amount) {
        return 1.0F / 16.0F * amount;
    }


    public static LayerDefinition createBurningLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(12, 5).addBox(2.0F, -2.0F, -8.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 7).addBox(2.0F, -3.0F, -8.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(1.0F, -4.0F, -8.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(6.0F, -15.0F, -8.0F, 1.0F, 13.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 0).addBox(7.0F, -15.0F, -8.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 3).addBox(1.0F, -15.0F, -8.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 14).addBox(4.0F, -10.0F, -8.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 13).addBox(3.0F, -11.0F, -8.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 9).addBox(2.0F, -12.0F, -8.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 5).addBox(1.0F, -13.0F, -8.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 0).addBox(-4.0F, -10.0F, -8.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static LayerDefinition createProcessingLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(14, 14).addBox(7.0F, -10.0F, -8.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 9).addBox(6.0F, -11.0F, -8.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 14).addBox(5.0F, -12.0F, -8.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 3).addBox(4.0F, -13.0F, -8.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 3).addBox(3.0F, -14.0F, -8.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(2.0F, -15.0F, -8.0F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0F, -10.0F, -8.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 3).addBox(-8.0F, -8.0F, -8.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 12).addBox(-8.0F, -15.0F, -8.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}
