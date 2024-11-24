package de.crafty.skylife.blockentities.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.skylife.blockentities.EndPortalCoreBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class EndPortalCoreRenderer implements BlockEntityRenderer<EndPortalCoreBlockEntity> {


    private final ItemRenderer itemRenderer;
    private final BlockEntityRenderDispatcher blockRenderer;

    public EndPortalCoreRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
        this.blockRenderer = ctx.getBlockEntityRenderDispatcher();
    }


    @Override
    public void render(EndPortalCoreBlockEntity blockEntity, float partialTicks, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (!blockEntity.hasAnimationStarted() || blockEntity.hasAnimationFinished())
            return;

        BlockPos pos = blockEntity.getBlockPos();

        float currentTick = blockEntity.getAnimationTick() + partialTicks;

        matrices.pushPose();
        matrices.translate(0.5D, 0.5D, 0.5D);

        Vec3[] sourceLocations = this.getEyeSourceLocations(pos);

        if (currentTick <= blockEntity.getPositioningAnimationTime()) {
            this.renderPositioningAnimation(blockEntity.getLevel(), sourceLocations, blockEntity.getBlockPos(), blockEntity.getPositioningAnimationSpeed(), currentTick, matrices, vertexConsumers, light, overlay, currentTick);
            matrices.popPose();
            return;
        }

        if (currentTick <= blockEntity.getPositioningAnimationTime() + blockEntity.getCircleAnimationTime()) {
            this.renderCircleAnimation(blockEntity.getLevel(), sourceLocations, pos, blockEntity.getCircleAnimationSpeed(), (float) (currentTick - blockEntity.getPositioningAnimationTime()), matrices, vertexConsumers, light, overlay, currentTick);
            matrices.popPose();
            return;
        }


        if (currentTick <= blockEntity.getPositioningAnimationTime() + blockEntity.getCircleAnimationTime() + blockEntity.getTransformationAnimationTime()) {
            this.renderTransformationAnimation(blockEntity.getLevel(), sourceLocations, blockEntity.getPortalFrameLocations(pos), pos, blockEntity.getTransformationAnimationSpeed(), (float) (currentTick - (blockEntity.getPositioningAnimationTime() + blockEntity.getCircleAnimationTime())), matrices, vertexConsumers, light, overlay, currentTick);
            matrices.popPose();
            return;
        }

        matrices.popPose();

    }

    private Vec3[] getEyeSourceLocations(BlockPos pos) {

        Vec3[] positions = new Vec3[12];

        double posX = pos.getX() + 0.5D;
        double posY = pos.getY() + 1.25D;
        double posZ = pos.getZ() + 0.5D;

        for (int i = 0; i < 12; i++) {

            double angle = Math.toRadians(360.0D / 12 * i);

            double xOff = Math.cos(angle) * 0.75F;
            double zOff = Math.sin(angle) * 0.75F;

            positions[i] = new Vec3(posX + xOff, posY, posZ + zOff);
        }

        return positions;
    }

    private void renderPositioningAnimation(Level world, Vec3[] eyeSourceLocations, BlockPos pos, double speed, float animationTick, PoseStack matrixStack, MultiBufferSource vertexConsumers, int packedLight, int packedOverlay, float currentTick) {
        Vec3 posVec = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

        for (Vec3 vec : eyeSourceLocations) {

            Vec3 pointingVec = vec.subtract(posVec);
            Vec3 positionVec = pointingVec.multiply(animationTick * speed, animationTick * speed, animationTick * speed);

            matrixStack.pushPose();
            matrixStack.translate(positionVec.x(), positionVec.y(), positionVec.z());
            matrixStack.pushPose();
            matrixStack.scale(0.5F, 0.5F, 0.75F);
            this.itemRenderer.renderStatic(new ItemStack(Items.ENDER_EYE), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumers, world, (int) pos.asLong());
            matrixStack.popPose();
            matrixStack.popPose();

        }
    }

    private void renderCircleAnimation(Level world, Vec3[] eyeSourceLocations, BlockPos pos, double rotationSpeed, float animationTick, PoseStack matrixStack, MultiBufferSource vertexConsumers, int packedLight, int packedOverlay, float currentTick) {
        Vec3 posVec = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D);

        for (Vec3 eyePosVec : eyeSourceLocations) {

            Vec3 vec = eyePosVec.subtract(posVec);
            vec = vec.yRot((float) Math.toRadians(animationTick * rotationSpeed));

            matrixStack.pushPose();
            matrixStack.translate(vec.x, 0.75D, vec.z);
            matrixStack.pushPose();
            matrixStack.scale(0.5F, 0.5F, 0.75F);
            this.itemRenderer.renderStatic(new ItemStack(Items.ENDER_EYE), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumers, world, (int) pos.asLong());
            matrixStack.popPose();
            matrixStack.popPose();
        }
    }

    private void renderTransformationAnimation(Level world, Vec3[] eyeSourceLocations, Vec3[] portalFrames, BlockPos pos, double speed, float animationTick, PoseStack matrixStack, MultiBufferSource vertexConsumers, int packedLight, int packedOverlay,  float currentTick) {

        for (int i = 0; i < 12; i++) {

            Vec3 portalFrameVec = portalFrames[i];
            Vec3 eyePosVec = eyeSourceLocations[i];

            Vec3 pointingVec = portalFrameVec.add(0.0D, 0.75D, 0.0D).subtract(eyePosVec);
            Vec3 sourcePointingVec = eyePosVec.subtract(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);

            double y = Math.sin(Math.toRadians(Math.min(180, 180.0D * speed * animationTick))) * 1.5D;
            Vec3 posVec = sourcePointingVec.add(pointingVec.multiply(animationTick * speed, 0.0D, animationTick * speed));


            matrixStack.pushPose();
            matrixStack.translate(posVec.x, 0.75D + y, posVec.z);
            matrixStack.pushPose();
            matrixStack.scale(0.5F, 0.5F, 0.75F);
            this.itemRenderer.renderStatic(new ItemStack(Items.ENDER_EYE), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumers, world, (int) pos.asLong());
            matrixStack.popPose();
            matrixStack.popPose();
        }
    }


}
