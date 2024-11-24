package de.crafty.skylife.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.crafty.skylife.client.model.entity.ResourceSheepEntityFurModel;
import de.crafty.skylife.client.model.entity.ResourceSheepEntityModel;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.registry.EntityRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

@Environment(EnvType.CLIENT)
public class ResourceSheepFurLayer extends RenderLayer<ResourceSheepEntity, ResourceSheepEntityModel<ResourceSheepEntity>> {
    private final ResourceSheepEntityFurModel<ResourceSheepEntity> model;

    public ResourceSheepFurLayer(RenderLayerParent<ResourceSheepEntity, ResourceSheepEntityModel<ResourceSheepEntity>> context, EntityModelSet loader) {
        super(context);
        this.model = new ResourceSheepEntityFurModel<>(loader.bakeLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP_FUR));
    }

    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, ResourceSheepEntity resourceSheep, float f, float g, float h, float j, float k, float l) {
        if (!resourceSheep.isSheared()) {
            if (resourceSheep.isInvisible()) {
                Minecraft minecraftClient = Minecraft.getInstance();
                boolean bl = minecraftClient.shouldEntityAppearGlowing(resourceSheep);
                if (bl) {
                    this.getParentModel().copyPropertiesTo(this.model);
                    this.model.animateModel(resourceSheep, f, g, h);
                    this.model.setAngles(resourceSheep, f, g, j, k, l);
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.outline(resourceSheep.getResourceType().getFurTexture()));
                    this.model.renderToBuffer(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlayCoords(resourceSheep, 0.0F), -16777216);
                }
                return;
            }
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, resourceSheep.getResourceType().getFurTexture(), matrixStack, vertexConsumerProvider, i, resourceSheep, f, g, j, k, l, h, -1);

        }
    }
}
