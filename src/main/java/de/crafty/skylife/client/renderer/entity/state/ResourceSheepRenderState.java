package de.crafty.skylife.client.renderer.entity.state;

import de.crafty.skylife.entity.ResourceSheepEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.DyeColor;

@Environment(EnvType.CLIENT)
public class ResourceSheepRenderState extends LivingEntityRenderState {
    public float headEatPositionScale;
    public float headEatAngleScale;
    public boolean isSheared;
    public ResourceSheepEntity.Type resourceType;
    public int id;

}
