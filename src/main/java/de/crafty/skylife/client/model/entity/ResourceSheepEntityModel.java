package de.crafty.skylife.client.model.entity;

import de.crafty.skylife.entity.ResourceSheepEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

@Environment(EnvType.CLIENT)
public class ResourceSheepEntityModel<T extends ResourceSheepEntity> extends QuadrupedModel<T> {
    private float headPitchModifier;

    public ResourceSheepEntityModel(ModelPart root) {
        super(root, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = QuadrupedModel.createBodyMesh(12, CubeDeformation.NONE);
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(PartNames.HEAD, CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 6.0F, -8.0F));
        modelPartData.addOrReplaceChild(PartNames.BODY, CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F));
        return LayerDefinition.create(modelData, 64, 32);
    }

    public void animateModel(T sheepEntity, float f, float g, float h) {
        super.prepareMobModel(sheepEntity, f, g, h);
        this.head.y = 6.0F + sheepEntity.getNeckAngle(h) * 9.0F;
        this.headPitchModifier = sheepEntity.getHeadAngle(h);
    }

    public void setAngles(T sheepEntity, float f, float g, float h, float i, float j) {
        super.setupAnim(sheepEntity, f, g, h, i, j);
        this.head.xRot = this.headPitchModifier;
    }
}
