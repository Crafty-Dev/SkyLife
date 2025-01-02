package de.crafty.skylife.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.crafty.skylife.client.model.entity.ResourceSheepEntityFurModel;
import de.crafty.skylife.client.model.entity.ResourceSheepEntityModel;
import de.crafty.skylife.client.renderer.entity.state.ResourceSheepRenderState;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.registry.EntityRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

@Environment(EnvType.CLIENT)
public class ResourceSheepFurLayer extends RenderLayer<ResourceSheepRenderState, ResourceSheepEntityModel> {
    private final EntityModel<ResourceSheepRenderState> adultModel;
    private final EntityModel<ResourceSheepRenderState> babyModel;

    public ResourceSheepFurLayer(RenderLayerParent<ResourceSheepRenderState, ResourceSheepEntityModel> renderLayerParent, EntityModelSet entityModelSet) {
        super(renderLayerParent);
        this.adultModel = new ResourceSheepEntityModel(entityModelSet.bakeLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP_FUR));
        this.babyModel = new ResourceSheepEntityModel(entityModelSet.bakeLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP_BABY_FUR));
    }


    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ResourceSheepRenderState entityRenderState, float f, float g) {
        if (!entityRenderState.isSheared) {
            EntityModel<ResourceSheepRenderState> entityModel = entityRenderState.isBaby ? this.babyModel : this.adultModel;

            if (entityRenderState.isInvisible) {
                boolean bl = entityRenderState.appearsGlowing;
                if (bl) {
                    entityModel.setupAnim(entityRenderState);
                    VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.outline(entityRenderState.resourceType.getFurTexture()));
                    entityModel.renderToBuffer(poseStack, vertexConsumer, i, LivingEntityRenderer.getOverlayCoords(entityRenderState, 0.0F), -16777216);
                }
            } else {
                coloredCutoutModelCopyLayerRender(entityModel, entityRenderState.resourceType.getFurTexture(), poseStack, multiBufferSource, i, entityRenderState, -1);
            }

        }
    }
}
