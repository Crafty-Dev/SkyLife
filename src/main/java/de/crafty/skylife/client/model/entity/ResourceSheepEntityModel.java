package de.crafty.skylife.client.model.entity;

import de.crafty.skylife.client.renderer.entity.state.ResourceSheepRenderState;
import de.crafty.skylife.entity.ResourceSheepEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.SheepRenderState;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class ResourceSheepEntityModel extends QuadrupedModel<ResourceSheepRenderState> {

    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 8.0F, 4.0F, 2.0F, 2.0F, 24.0F, Set.of("head"));

    public ResourceSheepEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = QuadrupedModel.createBodyMesh(12, CubeDeformation.NONE);
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild(
                "head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 6.0F, -8.0F)
        );
        partDefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    public static LayerDefinition createBabyBodyLayer(){
        return createBodyLayer().apply(BABY_TRANSFORMER);
    }

    public void setupAnim(ResourceSheepRenderState sheepRenderState) {
        super.setupAnim(sheepRenderState);
        this.head.y = this.head.y + sheepRenderState.headEatPositionScale * 9.0F * sheepRenderState.ageScale;
        this.head.xRot = sheepRenderState.headEatAngleScale;
    }
}
