package de.crafty.skylife.client.renderer.entity;

import de.crafty.skylife.client.model.entity.ResourceSheepEntityModel;
import de.crafty.skylife.client.renderer.entity.layers.ResourceSheepFurLayer;
import de.crafty.skylife.client.renderer.entity.state.ResourceSheepRenderState;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.registry.EntityRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ResourceSheepRenderer extends AgeableMobRenderer<ResourceSheepEntity, ResourceSheepRenderState, ResourceSheepEntityModel> {



    public ResourceSheepRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceSheepEntityModel(context.bakeLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP)), new ResourceSheepEntityModel(context.bakeLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP_BABY)), 0.7F);
        this.addLayer(new ResourceSheepFurLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceSheepRenderState createRenderState() {
        return new ResourceSheepRenderState();
    }


    @Override
    public void extractRenderState(ResourceSheepEntity sheep, ResourceSheepRenderState resourceSheepRenderState, float f) {
        super.extractRenderState(sheep, resourceSheepRenderState, f);
        resourceSheepRenderState.headEatAngleScale = sheep.getHeadEatAngleScale(f);
        resourceSheepRenderState.headEatPositionScale = sheep.getHeadEatPositionScale(f);
        resourceSheepRenderState.isSheared = sheep.isSheared();
        resourceSheepRenderState.resourceType = sheep.getResourceType();
        resourceSheepRenderState.id = sheep.getId();
    }

    @Override
    public ResourceLocation getTextureLocation(ResourceSheepRenderState livingEntityRenderState) {
        return livingEntityRenderState.resourceType.getTexture();
    }
}
