package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.blockentities.machines.integrated.SolarPanelBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class SolarPanelRenderer extends SimpleEnergyBlockRenderer<SolarPanelBlockEntity> {


    public SolarPanelRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(SolarPanelBlockEntity solarPanelBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        this.renderIOSideCentered(solarPanelBlockEntity.getBlockState(), Direction.DOWN, poseStack, multiBufferSource, light, overlay);
    }
}
