package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.blockentities.machines.integrated.FluxFurnaceBlockEntity;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class FluxFurnaceRenderer extends SimpleEnergyBlockRenderer<FluxFurnaceBlockEntity> {


    public FluxFurnaceRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(FluxFurnaceBlockEntity fluxFurnaceBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.isHolding(ItemRegistry.MACHINE_KEY)) {
            for (Direction side : Direction.values()) {
                this.renderIOSideCentered(fluxFurnaceBlockEntity.getBlockState(), side, poseStack, multiBufferSource, light, overlay);
            }
        }


    }
}
