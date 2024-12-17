package de.crafty.skylife.blockentities.renderer.machines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.energy.blockentity.renderer.SimpleEnergyBlockRenderer;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.block.machines.integrated.BlockBreakerBlock;
import de.crafty.skylife.blockentities.machines.integrated.BlockBreakerBlockEntity;
import de.crafty.skylife.registry.EntityRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class BlockBreakerRenderer extends SimpleEnergyBlockRenderer<BlockBreakerBlockEntity> {

    //Static implementation for performance efficiency
    public static final ResourceLocation WOOD = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/block_breaker/drill_wood.png");
    public static final ResourceLocation STONE = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/block_breaker/drill_stone.png");
    public static final ResourceLocation IRON = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/block_breaker/drill_iron.png");
    public static final ResourceLocation GOLD = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/block_breaker/drill_gold.png");
    public static final ResourceLocation DIAMOND = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/block_breaker/drill_diamond.png");
    public static final ResourceLocation NETHERITE = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/machines/block_breaker/drill_netherite.png");

    private final ModelPart chain;

    public BlockBreakerRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);

        this.chain = ctx.bakeLayer(EntityRegistry.ModelLayers.BLOCK_BREAKER_CHAIN).getChild("main");
    }

    @Override
    public void render(BlockBreakerBlockEntity blockBreakerBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        Level level = blockBreakerBlockEntity.getLevel();
        if(level == null)
            return;

        BlockState state = blockBreakerBlockEntity.getBlockState();
        BlockPos pos = blockBreakerBlockEntity.getBlockPos();
        BlockPos targetPos = blockBreakerBlockEntity.getTargetPos();
        BlockState targetState = level.getBlockState(targetPos);

        /*if(!Minecraft.getInstance().player.isCrouching())
            return;
         */

        Direction facing = blockBreakerBlockEntity.getBlockState().getValue(BaseEnergyBlock.FACING);

        for (Direction direction : Direction.values()) {
            this.renderIOSideCentered(blockBreakerBlockEntity.getBlockState(), direction, poseStack, multiBufferSource, light, overlay);
        }

        float rotation = ((blockBreakerBlockEntity.getLevel().getGameTime() + partialTicks) % 180.0F);
        ResourceLocation drill_texture = this.getToolTypeSpecificTexture(blockBreakerBlockEntity.getToolType());
        if(drill_texture == null)
            return;

        poseStack.pushPose();
        this.translateByDirection(facing, poseStack);
        poseStack.translate(0.5F, 0.5F, -(1.0F + (0.5F - 1.5F / 16.0F)));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.pushPose();
        if(blockBreakerBlockEntity.getStoredEnergy() >= (blockBreakerBlockEntity.isIdling() ? 10 : blockBreakerBlockEntity.getToolType().getEnergyConsumption()))
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 8.0F));
        this.chain.render(poseStack, multiBufferSource.getBuffer(RenderType.entitySolid(drill_texture)), light, overlay);
        poseStack.popPose();
        poseStack.popPose();


        if(blockBreakerBlockEntity.getDestroyProgress() < 0)
            return;

        Vec3i targetDirection = state.getValue(BlockBreakerBlock.FACING).getNormal();

        poseStack.pushPose();
        poseStack.translate(targetDirection.getX(), targetDirection.getY(), targetDirection.getZ());
        VertexConsumer vertexConsumer2 = new SheetedDecalTextureGenerator(
                Minecraft.getInstance().renderBuffers().crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get((int) (blockBreakerBlockEntity.getDestroyProgress() * 10.0F))),
                poseStack.last(),
                1.0F);

        Minecraft.getInstance().getBlockRenderer().renderBreakingTexture(targetState, targetPos, level, poseStack, vertexConsumer2);
        poseStack.popPose();
    }

    protected void translateByDirection(Direction direction, PoseStack poseStack) {
        if (direction == Direction.EAST) {
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            poseStack.translate(0.0F, 0.0F, -1.0F);
        }

        if (direction == Direction.SOUTH) {
            poseStack.translate(1.0F, 0.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }

        if (direction == Direction.WEST) {
            poseStack.translate(0.0F, 0.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        }

        if (direction == Direction.UP) {
            poseStack.translate(0.0F, 1.0F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }

        if (direction == Direction.DOWN) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.translate(0.0F, -1.0F, 0.0F);
        }

    }


    private ResourceLocation getToolTypeSpecificTexture(BlockBreakerBlockEntity.ToolType toolType) {
        switch (toolType){
            case WOOD -> {
                return WOOD;
            }
            case STONE -> {
                return STONE;
            }
            case IRON -> {
                return IRON;
            }
            case GOLD -> {
                return GOLD;
            }
            case DIAMOND -> {
                return DIAMOND;
            }
            case NETHERITE -> {
                return NETHERITE;
            }
        }
        return null;
    }

    public static LayerDefinition createChainLayer() {

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 5).addBox(-2.5F, -1.0F, 2.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(-2.5F, -1.0F, -5.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(20, 24).addBox(2.5F, -1.0F, 2.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 27).addBox(-4.5F, -1.0F, 2.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 27).addBox(-4.5F, -1.0F, -4.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 4).addBox(2.5F, -1.0F, -4.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 4).addBox(2.5F, -2.0F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 9).addBox(-4.5F, -2.0F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 19).addBox(2.0F, -3.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 22).addBox(-3.0F, -3.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition drill_layer_05_r1 = bb_main.addOrReplaceChild("drill_layer_05_r1", CubeListBuilder.create().texOffs(12, 17).addBox(-0.5F, -5.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 24).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -4.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 24).addBox(2.0F, -3.75F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(22, 19).addBox(-3.0F, -3.75F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 16).addBox(-4.0F, -2.75F, 2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 13).addBox(-4.0F, -2.75F, -4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 10).addBox(2.0F, -2.75F, -4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 7).addBox(2.0F, -2.75F, 2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(2.5F, -2.75F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 14).addBox(-4.5F, -2.75F, -2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 0).addBox(-2.5F, -1.75F, -5.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 13).addBox(-2.5F, -1.75F, 2.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.75F, 0.0F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
