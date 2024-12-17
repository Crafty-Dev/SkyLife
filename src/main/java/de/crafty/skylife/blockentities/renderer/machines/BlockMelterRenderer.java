package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.blockentities.machines.integrated.BlockMelterBlockEntity;
import de.crafty.skylife.util.RenderUtils;
import de.crafty.skylife.util.SkyLifeRenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class BlockMelterRenderer extends SimpleEnergyBlockRenderer<BlockMelterBlockEntity> {


    private final ItemRenderer itemRenderer;

    public BlockMelterRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);

        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(BlockMelterBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        if(blockEntity.getLevel() == null)
            return;

        this.renderIOSideCentered(blockEntity.getBlockState(), Direction.DOWN, poseStack, multiBufferSource, light, overlay);

        //Block Rendering

        if(blockEntity.getMeltingBlock() != Blocks.AIR){
            float r = 255 / 255.0F;
            float g = (255 - blockEntity.getMeltingProgress() * 150) / 255.0F;
            float b = (255 - blockEntity.getMeltingProgress() * 150) / 255.0F;

            poseStack.pushPose();
            poseStack.translate(px(5.5F), px(15.0F), px(5.5F));
            poseStack.scale(px(5.0F), px(5.0F), px(5.0F));
            SkyLifeRenderUtils.renderColoredSolidBlock(blockEntity.getMeltingBlock().defaultBlockState(), blockEntity.getBlockPos(), blockEntity.getLevel(), r, g, b, poseStack, multiBufferSource, overlay);
            poseStack.popPose();
        }


        //Fluid rendering ---------

        if (blockEntity.getFluid() == Fluids.EMPTY)
            return;

        TextureAtlasSprite spriteStill = FluidRenderHandlerRegistryImpl.INSTANCE.get(blockEntity.getFluid()).getFluidSprites(blockEntity.getLevel(), null, blockEntity.getFluid().defaultFluidState())[0];
        int color = blockEntity.getFluid() == Fluids.WATER ? new Color(BiomeColors.getAverageWaterColor(blockEntity.getLevel(), blockEntity.getBlockPos())).getRGB() : -1;

        float fillStatus = (float) blockEntity.getVolume() / (float) blockEntity.getFluidCapacity();

        //Top
        if (fillStatus < 1.0F)
            RenderUtils.renderTexturedPlane(
                    poseStack.last(),
                    multiBufferSource.getBuffer(RenderType.translucent()),
                    spriteStill,
                    Direction.UP,
                    px(1.0F + 0.01F),
                    px(1.01F + (10.0F - 0.02F) * fillStatus),
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
                px(1.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((10.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((10.0F - 0.02F) * fillStatus),
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
                px(14.0F - 0.02F),
                px((10.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((10.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.EAST,
                px(15.0F - 0.01F),
                px(1.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((10.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((10.0F - 0.02F) * fillStatus),
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
                px((10.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((10.0F - 0.02F) * fillStatus),
                color,
                light
        );
    }

    private float px(float amount) {
        return 1.0F / 16.0F * amount;
    }


}
