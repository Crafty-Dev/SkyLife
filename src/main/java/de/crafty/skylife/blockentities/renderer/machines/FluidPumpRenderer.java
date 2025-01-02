package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.blockentities.machines.integrated.FluidPumpBlockEntity;
import de.crafty.skylife.registry.ItemRegistry;
import de.crafty.skylife.util.RenderUtils;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluids;

import java.awt.*;

public class FluidPumpRenderer extends SimpleEnergyBlockRenderer<FluidPumpBlockEntity> {

    public FluidPumpRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(FluidPumpBlockEntity fluidPump, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        if(fluidPump.getLevel() == null)
            return;

        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.isHolding(ItemRegistry.MACHINE_KEY)) {
            for (Direction side : Direction.values()) {
                this.renderIOSideCentered(fluidPump.getBlockState(), side, poseStack, multiBufferSource, light, overlay);
            }
        }

        if(fluidPump.getFluid() == Fluids.EMPTY)
            return;

        TextureAtlasSprite spriteStill = FluidRenderHandlerRegistryImpl.INSTANCE.get(fluidPump.getFluid()).getFluidSprites(fluidPump.getLevel(), null, fluidPump.getFluid().defaultFluidState())[0];
        int color = fluidPump.getFluid() == Fluids.WATER ? new Color(BiomeColors.getAverageWaterColor(fluidPump.getLevel(), fluidPump.getBlockPos())).getRGB() : -1;

        float fillStatus = (float) fluidPump.getVolume() / (float) fluidPump.getFluidCapacity();

        if (fillStatus < 1.0F)
            RenderUtils.renderTexturedPlane(
                    poseStack.last(),
                    multiBufferSource.getBuffer(RenderType.translucent()),
                    spriteStill,
                    Direction.UP,
                    px(1.0F + 0.01F),
                    px(4.01F + (8.0F - 0.02F) * fillStatus),
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
                px(4.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((8.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((8.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.SOUTH,
                px(1.0F + 0.01F),
                px(4.0F + 0.01F),
                px(15.0F - 0.01F),
                px(14.0F - 0.02F),
                px((8.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((8.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.EAST,
                px(15.0F - 0.01F),
                px(4.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((8.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((8.0F - 0.02F) * fillStatus),
                color,
                light
        );

        RenderUtils.renderTexturedPlane(
                poseStack.last(),
                multiBufferSource.getBuffer(RenderType.translucent()),
                spriteStill,
                Direction.WEST,
                px(1.0F + 0.01F),
                px(4.0F + 0.01F),
                px(1.0F + 0.01F),
                px(14.0F - 0.02F),
                px((8.0F - 0.02F) * fillStatus),
                px(1.0F + 0.01F),
                px(0.01F),
                px(14.0F - 0.02F),
                px((8.0F - 0.02F) * fillStatus),
                color,
                light
        );
    }

    private float px(float amount) {
        return 1.0F / 16.0F * amount;
    }
}
