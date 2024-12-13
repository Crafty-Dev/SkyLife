package de.crafty.skylife.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class RenderUtils {


    /*
    * Thanks to enderio for having solved this in their code :D
    * */
    public static void renderTexturedPlane(PoseStack.Pose pose, VertexConsumer consumer, TextureAtlasSprite texture, Direction facing, float x, float y, float z, float width, float height, float u, float v, float texWidth, float texHeight, int color, int light) {

        float u0 = u * texture.contents().width() / 16.0F;
        float v0 = v * texture.contents().height() / 16.0F;

        float u1 = u0 + texWidth * texture.contents().width() / 16.0F;
        float v1 = v0 + texHeight * texture.contents().height() / 16.0F;

        Vec3i normal = facing.getNormal();
        float xNormal = normal.getX();
        float yNormal = normal.getY();
        float zNormal = normal.getZ();

        if(facing == Direction.UP) {
            consumer.addVertex(pose, x, y, z).setColor(color).setUv(texture.getU(u0), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x, y, z + height).setColor(color).setUv(texture.getU(u0), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x + width, y, z + height).setColor(color).setUv(texture.getU(u1), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x + width, y, z).setColor(color).setUv(texture.getU(u1), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
        }

        if(facing == Direction.DOWN) {
            consumer.addVertex(pose, x, y, z).setColor(color).setUv(texture.getU(u0), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x + width, y, z).setColor(color).setUv(texture.getU(u1), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x + width, y, z + height).setColor(color).setUv(texture.getU(u1), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x, y, z + height).setColor(color).setUv(texture.getU(u0), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
        }


        if(facing == Direction.NORTH) {
            consumer.addVertex(pose, x, y, z).setColor(color).setUv(texture.getU(u1), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x, y + height, z).setColor(color).setUv(texture.getU(u1), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x + width, y + height, z).setColor(color).setUv(texture.getU(u0), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x + width, y, z).setColor(color).setUv(texture.getU(u0), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
        }

        if(facing == Direction.SOUTH) {
            consumer.addVertex(pose, x, y, z).setColor(color).setUv(texture.getU(u1), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x + width, y, z).setColor(color).setUv(texture.getU(u0), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x + width, y + height, z).setColor(color).setUv(texture.getU(u0), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x, y + height, z).setColor(color).setUv(texture.getU(u1), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);

        }

        if(facing == Direction.WEST) {
            consumer.addVertex(pose, x, y, z).setColor(color).setUv(texture.getU(u1), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x, y, z + width).setColor(color).setUv(texture.getU(u0), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x , y + height, z + width).setColor(color).setUv(texture.getU(u0), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x, y + height, z).setColor(color).setUv(texture.getU(u1), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
        }

        if(facing == Direction.EAST) {
            consumer.addVertex(pose, x, y, z).setColor(color).setUv(texture.getU(u1), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x, y + height, z).setColor(color).setUv(texture.getU(u1), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x , y + height, z + width).setColor(color).setUv(texture.getU(u0), texture.getV(v0)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
            consumer.addVertex(pose, x, y, z + width).setColor(color).setUv(texture.getU(u0), texture.getV(v1)).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(xNormal, yNormal, zNormal);
        }

    }

}
