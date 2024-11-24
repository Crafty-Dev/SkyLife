package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.block.machines.SkyLifeEnergyStorageBlock;
import de.crafty.skylife.blockentities.machines.integrated.BriquetteGeneratorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class BriquetteGeneratorRenderer extends SimpleEnergyBlockRenderer<BriquetteGeneratorBlockEntity> {


    public BriquetteGeneratorRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(BriquetteGeneratorBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        this.renderIOSideCentered(blockEntity.getBlockState(), Direction.UP, poseStack, multiBufferSource, light, overlay);
    }
}
