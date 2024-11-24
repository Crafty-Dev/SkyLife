package de.crafty.skylife.blockentities.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.crafty.skylife.blockentities.GraveStoneBlockEntity;
import de.crafty.skylife.registry.SkyLifeRenderLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class GraveStoneRenderer implements BlockEntityRenderer<GraveStoneBlockEntity> {


    private final GuiGraphics drawContext;
    private Supplier<PlayerSkin> texturesSupplier;

    public GraveStoneRenderer(BlockEntityRendererProvider.Context ctx) {
        this.drawContext = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
    }

    @Override
    public void render(GraveStoneBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (blockEntity.getOwner() == null)
            return;

        if (this.texturesSupplier == null)
            this.texturesSupplier = Minecraft.getInstance().getSkinManager().lookupInsecure(blockEntity.getOwner());


        Font textRenderer = Minecraft.getInstance().font;
        GameProfile gameProfile = blockEntity.getOwner();

        FormattedCharSequence playerName = Component.nullToEmpty(gameProfile.getName()).getVisualOrderText();


        matrices.pushPose();
        matrices.translate(0.5D, 0.9D, 0.835D);
        matrices.mulPose(new Quaternionf().rotateY(Mth.PI));
        matrices.scale(0.0085F, -0.0085F, 0.0085F);
        textRenderer.drawInBatch8xOutline(playerName, -textRenderer.width(playerName) / 2.0F, 0.0F, ChatFormatting.BLACK.getColor(), ChatFormatting.WHITE.getColor(), matrices.last().pose(), vertexConsumers, light);
        matrices.popPose();


        //matrices.push();
        //matrices.translate(0.5D, 0.775D, 0.835D);
        //matrices.multiply(new Quaternionf().rotateY(MathHelper.PI));
        //matrices.scale(0.065F, -0.065F, 0.065F);
        float x1 = 0.0F;
        float x2 = 1.0F;
        float y1 = 0.0F;
        float y2 = 1.0F;

        float u1 = 1 / 64.0F * 8;
        float u2 = 1 / 64.0F * 16;
        float v1 = 1 / 64.0F * 8;
        float v2 = 1 / 64.0F * 16;

        float z = 0.0F;
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        float alpha = 1.0F;

        float partialLight = blockEntity.getLevel().getDayTime();

        matrices.pushPose();
        matrices.translate(0.75D, 0.775D, 0.84D);
        matrices.mulPose(new Quaternionf().rotateY(Mth.PI));
        matrices.scale(0.5F, -0.5F, 1.0F);
        Matrix4f matrix4f = matrices.last().pose();

        //Trick 17 (Use of TextRenderLayer to get correct vertex Consumer)
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SkyLifeRenderLayers.TEXTURE3DCONTEXT.apply(this.texturesSupplier.get().texture()));
        vertexConsumer.addVertex(matrix4f, x1, y1, z).setUv(u1, v1).setColor(red, green, blue, alpha).setLight(light);
        vertexConsumer.addVertex(matrix4f, x1, y2, z).setUv(u1, v2).setColor(red, green, blue, alpha).setLight(light);
        vertexConsumer.addVertex(matrix4f, x2, y2, z).setUv(u2, v2).setColor(red, green, blue, alpha).setLight(light);
        vertexConsumer.addVertex(matrix4f, x2, y1, z).setUv(u2, v1).setColor(red, green, blue, alpha).setLight(light);

        matrices.popPose();
        //matrices.pop();
    }
}
