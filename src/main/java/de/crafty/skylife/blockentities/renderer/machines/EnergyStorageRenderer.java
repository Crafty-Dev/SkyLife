package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.block.machines.SkyLifeEnergyStorageBlock;
import de.crafty.skylife.blockentities.machines.SkyLifeEnergyStorageBlockEntity;
import de.crafty.skylife.registry.EntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

public class EnergyStorageRenderer extends SimpleEnergyBlockRenderer<SkyLifeEnergyStorageBlockEntity> {

    public static final ResourceLocation LOW_CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/energy_storage/energy_core_low.png");
    public static final ResourceLocation MEDIUM_CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/energy_storage/energy_core_medium.png");
    public static final ResourceLocation HIGH_CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/energy_storage/energy_core_high.png");

    private final ModelPart coreModel;

    public EnergyStorageRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);

        this.coreModel = ctx.bakeLayer(EntityRegistry.ModelLayers.ENERGY_STORAGE_CORE).getChild("main");
    }


    @Override
    public void render(SkyLifeEnergyStorageBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        if (this.getStorageSpecificTexture(blockEntity) == null)
            return;

        BlockState state = blockEntity.getBlockState();
        if(!(state.getBlock() instanceof SkyLifeEnergyStorageBlock))
            return;

        for(Direction side : Direction.values()){
            this.renderIOSideCentered(state, side, poseStack, multiBufferSource, light, overlay);
        }

        if (blockEntity.getStoredEnergy() == 0)
            return;

        RenderSystem.depthMask(false);
        float filled = blockEntity.getStoredEnergy() / ((float) blockEntity.getTier().getCapacity());
        float transparency = 0.25F + (0.75F * filled);
        Color color = new Color(1.0F, 1.0F, 1.0F, transparency);
        poseStack.pushPose();
        poseStack.translate(0.5F, -0.625F, 0.5F);
        poseStack.rotateAround(Axis.YP.rotationDegrees(blockEntity.getLevel().getGameTime() % 360), 0.0F, 1.0F, 0.0F);
        this.coreModel.render(poseStack, multiBufferSource.getBuffer(RenderType.entityTranslucent(this.getStorageSpecificTexture(blockEntity))), light, overlay, color.getRGB());
        poseStack.popPose();

        Direction direction = blockEntity.getBlockState().getValue(SkyLifeEnergyStorageBlock.FACING);

        Font font = Minecraft.getInstance().font;
        Component progress = Component.literal((int) (filled * 100) + "%");
        poseStack.pushPose();
        this.translateByDirection(direction, poseStack);
        poseStack.translate(0.5F, 0.625F, -0.000125F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.scale(0.015625F * 0.65F, -0.015625F * 0.65F, 0.015625F * 0.65F);
        font.drawInBatch(progress, -font.width(progress) / 2.0F + 0.5F, -font.lineHeight / 2.0F, -1, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, light);
        poseStack.popPose();

        RenderSystem.depthMask(true);

    }

    private ResourceLocation getStorageSpecificTexture(SkyLifeEnergyStorageBlockEntity blockEntity) {
        return switch (blockEntity.getTier()) {
            case LOW -> LOW_CORE_TEXTURE;
            case MEDIUM -> MEDIUM_CORE_TEXTURE;
            case HIGH -> HIGH_CORE_TEXTURE;
        };
    }

    public static LayerDefinition createCoreLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -12.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

}
