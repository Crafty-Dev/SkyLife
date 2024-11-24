package de.crafty.skylife.client.renderer.entity;

import de.crafty.skylife.client.model.entity.ResourceSheepEntityModel;
import de.crafty.skylife.client.renderer.entity.layers.ResourceSheepFurLayer;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.registry.EntityRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ResourceSheepRenderer extends MobRenderer<ResourceSheepEntity, ResourceSheepEntityModel<ResourceSheepEntity>> {
    public ResourceSheepRenderer(EntityRendererProvider.Context context) {
        super(context, new ResourceSheepEntityModel<>(context.bakeLayer(EntityRegistry.ModelLayers.RESOURCE_SHEEP)), 0.7F);
        this.addLayer(new ResourceSheepFurLayer(this, context.getModelSet()));
    }

    public @NotNull ResourceLocation getTextureLocation(ResourceSheepEntity resourceSheep) {
        return resourceSheep.getResourceType().getTexture();
    }
}
