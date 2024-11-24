package de.crafty.skylife.blockentities.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.skylife.blockentities.MagicalWorkbenchBlockEntity;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class MagicalWorkbenchRenderer implements BlockEntityRenderer<MagicalWorkbenchBlockEntity> {


    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderer;


    public MagicalWorkbenchRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
        this.entityRenderer = ctx.getEntityRenderer();
    }

    @Override
    public void render(MagicalWorkbenchBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (!blockEntity.hasAnimationStarted() || blockEntity.hasAnimationFinished())
            return;


        float currentTick = blockEntity.getAnimationTick() + partialTicks;

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 srcPosVec = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

        matrixStack.pushPose();

        double morphAnimationSpeed = 0.005D;
        double morphAnimationTime = 1 / morphAnimationSpeed;

        if (currentTick <= morphAnimationTime) {
            this.renderMorphAnimation(blockEntity, morphAnimationSpeed, currentTick, matrixStack, vertexConsumers, light, overlay);
            matrixStack.popPose();
            return;
        }

        double transformationAnimationSpeed = 0.005D;
        double transformationAnimationTime = 1 / transformationAnimationSpeed;

        if (currentTick <= morphAnimationTime + transformationAnimationTime) {
            this.renderTransformationAnimation(blockEntity, 0.1F * Math.pow(morphAnimationTime, 2), transformationAnimationSpeed, (float) (currentTick - morphAnimationTime), matrixStack, vertexConsumers, light, overlay);
            matrixStack.popPose();
            return;
        }


        matrixStack.popPose();
    }

    private void renderMorphAnimation(MagicalWorkbenchBlockEntity blockEntity, double speed, float animationTick, PoseStack matrixStack, MultiBufferSource vertexConsumers, int packedLight, int packedOverlay) {

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 srcPosVecInt = new Vec3(pos.getX(), pos.getY(), pos.getZ());


        for (BlockPos p : blockEntity.getRitualBlocks()) {

            Vec3 posVec = new Vec3(p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D);
            Vec3 pointingVec = posVec.subtract(srcPosVecInt);

            Vec3 moveVec = new Vec3(0.0D, 2.5D, 0.0D);

            matrixStack.pushPose();
            matrixStack.translate(pointingVec.x(), pointingVec.y() + moveVec.multiply(0.0D, speed * animationTick, 0.0D).y(), pointingVec.z());
            matrixStack.pushPose();

            float scale = (float) (2.0F - (1.5F * speed * animationTick));

            matrixStack.scale(scale, scale, scale);
            matrixStack.pushPose();
            matrixStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians((0.1F * Math.pow(animationTick, 2)))));
            this.itemRenderer.renderStatic(blockEntity.getRitualItems()[blockEntity.getRitualBlocks().indexOf(p)], ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumers, blockEntity.getLevel(), (int) pos.asLong());
            matrixStack.popPose();
            matrixStack.popPose();
            matrixStack.popPose();

        }

    }

    private void renderTransformationAnimation(MagicalWorkbenchBlockEntity blockEntity, double rotationSpeed, double speed, float animationTick, PoseStack matrixStack, MultiBufferSource vertexConsumers, int packedLight, int packedOverlay) {

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 srcPosVecInt = new Vec3(pos.getX(), pos.getY(), pos.getZ());

        for(BlockPos p : blockEntity.getRitualBlocks()){

            Vec3 posVec = new Vec3(p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D);
            Vec3 pointingVec = posVec.subtract(srcPosVecInt).add(0.0D, 2.5D, 0.0D);

            Vec3 moveVec = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D + 5.0D, pos.getZ() + 0.5D).subtract(posVec.add(0.0D, 2.5D, 0.0D)).multiply(new Vec3(speed * animationTick, speed * animationTick, speed * animationTick));

            matrixStack.pushPose();
            matrixStack.translate(pointingVec.x(), pointingVec.y(), pointingVec.z());
            matrixStack.pushPose();
            matrixStack.translate(moveVec.x(), moveVec.y(), moveVec.z());
            matrixStack.pushPose();
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.pushPose();
            matrixStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(rotationSpeed * animationTick)));
            this.itemRenderer.renderStatic(blockEntity.getRitualItems()[blockEntity.getRitualBlocks().indexOf(p)], ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumers, blockEntity.getLevel(), (int) pos.asLong());
            matrixStack.popPose();
            matrixStack.popPose();
            matrixStack.popPose();
            matrixStack.popPose();

        }


        Vec3 moveVec = new Vec3(pos.getX() + 0.5D, pos.getY() + 5.5D, pos.getZ() + 0.5D).subtract(srcPosVecInt.add(0.5D, 0.5D, 0.5D)).multiply(new Vec3(speed * animationTick, speed * animationTick, speed * animationTick));

        matrixStack.pushPose();
        matrixStack.translate(0.5D + moveVec.x(), 0.5D + moveVec.y(), 0.5D + moveVec.z());
        matrixStack.pushPose();
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        matrixStack.pushPose();
        matrixStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(0.4F * Math.pow(animationTick, 2))));
        this.itemRenderer.renderStatic(new ItemStack(ItemRegistry.DRAGON_ARTIFACT), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumers, blockEntity.getLevel(), (int) pos.asLong());
        matrixStack.popPose();
        matrixStack.popPose();
        matrixStack.popPose();

    }
}
