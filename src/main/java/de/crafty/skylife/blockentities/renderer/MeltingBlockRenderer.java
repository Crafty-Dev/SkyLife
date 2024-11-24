package de.crafty.skylife.blockentities.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.skylife.block.MeltingBlock;
import de.crafty.skylife.blockentities.MeltingBlockEntity;
import de.crafty.skylife.util.SkyLifeRenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

@Environment(EnvType.CLIENT)
public class MeltingBlockRenderer implements BlockEntityRenderer<MeltingBlockEntity> {

    public MeltingBlockRenderer(BlockEntityRendererProvider.Context ctx) {

    }


    @Override
    public void render(MeltingBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {

        if (blockEntity.getLevel() == null)
            return;

        float r = 255 / 255.0F;
        float g = (255 - blockEntity.getProgress() * 150) / 255.0F;
        float b = (255 - blockEntity.getProgress() * 150) / 255.0F;

        SkyLifeRenderUtils.renderColoredSolidBlock(((MeltingBlock)blockEntity.getBlockState().getBlock()).getBlockType().defaultBlockState(), blockEntity.getBlockPos(), blockEntity.getLevel(), r, g, b, matrices, vertexConsumers, overlay);
    }

}
