package de.crafty.skylife.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Matrix4f;

public class RenderUtils {


    /*
    * Thanks to enderio for having solved this in their code :D
    * */
    public static void renderTexturedPlane(PoseStack.Pose pose, VertexConsumer consumer, TextureAtlasSprite texture, Direction facing, float x, float y, float z, float width, float height, float u, float v, float texWidth, float texHeight, int color, int light) {

        texture = Minecraft.getInstance().getTextureAtlas(texture.atlasLocation()).apply(texture.contents().name());

        float u0 = u * texture.contents().width() / 16.0F;
        float v0 = v * texture.contents().height() / 16.0F;

        float u1 = u0 + texWidth * texture.contents().width() / 16.0F;
        float v1 = v0 + texHeight * texture.contents().height() / 16.0F;

        Vec3i normal = facing.getUnitVec3i();
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


    public static void renderGuiSprite(GuiGraphics guiGraphics, TextureAtlasSprite sprite, int x, int y, int width, int height, float u0, float v0, float uWidth, float vWidth) {

        float spriteWidthFloat = (sprite.getU1() - sprite.getU0());
        float spriteHeightFloat = (sprite.getV1() - sprite.getV0());

        u0 = u0 / sprite.contents().width() * spriteWidthFloat;
        v0 = v0 / sprite.contents().height() * spriteHeightFloat;

        uWidth = uWidth / sprite.contents().width() * spriteWidthFloat;
        vWidth = vWidth / sprite.contents().height() * spriteHeightFloat;

        guiGraphics.pose().pushPose();
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());

        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, x, y, 0).setUv(sprite.getU0() + u0, sprite.getV0() + v0);
        bufferBuilder.addVertex(matrix4f, x, y + height, 0).setUv(sprite.getU0() + u0, sprite.getV0() + v0 + vWidth);
        bufferBuilder.addVertex(matrix4f, x + width, y + height, 0).setUv(sprite.getU0() + u0 + uWidth, sprite.getV0() + v0 + vWidth);
        bufferBuilder.addVertex(matrix4f, x + width, y, 0).setUv(sprite.getU0() + u0 + uWidth, sprite.getV0() + v0);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        guiGraphics.pose().popPose();
    }

}
