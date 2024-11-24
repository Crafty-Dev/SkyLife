package de.crafty.skylife.blockentities.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.skylife.block.LeafPressBlock;
import de.crafty.skylife.blockentities.LeafPressBlockEntity;
import de.crafty.skylife.registry.BlockRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class LeafPressRenderer implements BlockEntityRenderer<LeafPressBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final BlockEntityRenderDispatcher blockRenderer;

    public LeafPressRenderer(BlockEntityRendererProvider.Context ctx){
        this.itemRenderer = ctx.getItemRenderer();
        this.blockRenderer = ctx.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(LeafPressBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource vertexConsumers, int combinedLight, int combinedOverlay) {

        if(blockEntity.getContent() == ItemStack.EMPTY)
            return;


        BlockState state = blockEntity.getBlockState();

        matrixStack.pushPose();

        float f = switch (state.getValue(LeafPressBlock.PROGRESS)) {
            case 2 -> 0.9F;
            case 3 -> 0.8F;
            case 4 -> 0.7F;
            case 5 -> 0.5F;
            case 6 -> 0.3F;
            default -> 1.0F;
        };

        if(state.getValue(LeafPressBlock.PROGRESS) == 0 && blockEntity.getContent().is(BlockRegistry.DRIED_LEAVES.asItem()))
            f = 0.4F;

        matrixStack.scale(1.25F, 1.25F * f, 1.25F);
        matrixStack.translate(0.5F / 1.25F, 0.4F / 1.25F, 0.5F / 1.25F);

        this.itemRenderer.renderStatic(blockEntity.getContent(), ItemDisplayContext.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumers, blockEntity.getLevel(), (int) blockEntity.getBlockPos().asLong());
        matrixStack.popPose();


    }
}
