package de.crafty.skylife.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.BitSet;
import java.util.List;

public class SkyLifeRenderUtils {


    //Utility Function provided to render a Block via BlockEntity
    //Compressed version of minecrafts method
    public static void renderColoredSolidBlock(BlockState state, BlockPos pos, Level world, float r, float g, float b, PoseStack matrices, MultiBufferSource vertexConsumers, int overlay) {

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(ItemBlockRenderTypes.getChunkRenderType(state));

        boolean ambientOcclusion = Minecraft.useAmbientOcclusion() && state.getLightEmission() == 0 && blockRenderer.getBlockModel(state).useAmbientOcclusion();

        RandomSource random = world.getRandom();

        ModelBlockRenderer.AmbientOcclusionFace calculator = new ModelBlockRenderer.AmbientOcclusionFace();

        float[] box = new float[Direction.values().length * 2];
        BitSet flags = new BitSet(3);

        //Render Quads that use specific directions
        for (Direction direction : Direction.values()) {
            random.setSeed(state.getSeed(pos));
            //List of Quads for this direction
            List<BakedQuad> quads = blockRenderer.getBlockModel(state).getQuads(state, direction, random);

            //Stop when culling says No :D
            if (!Block.shouldRenderFace(state, world.getBlockState(pos.relative(direction)), direction))
                continue;
            //Default light used when ambientOcclusion is deactivated
            int light = LevelRenderer.getLightColor(world, state, pos.relative(direction));

            for (BakedQuad quad : quads) {

                //Default brightness used when ambientOcclusion is deactivated
                float brightness = world.getShade(quad.getDirection(), quad.isShade());

                if (ambientOcclusion) {
                    blockRenderer.getModelRenderer().calculateShape(world, state, pos, quad.getVertices(), quad.getDirection(), box, flags);
                    calculator.calculate(world, state, pos, quad.getDirection(), box, flags, quad.isShade());
                }

                //Render the Quad
                SkyLifeRenderUtils.renderQuad(vertexConsumer, matrices, quad, ambientOcclusion, calculator, r, g, b, brightness, light, overlay);

            }
        }

        //Render Quads that should always be rendered
        random.setSeed(state.getSeed(pos));
        for (BakedQuad quad : blockRenderer.getBlockModel(state).getQuads(state, null, random)) {
            //Default brightness used when ambienOcclusion is deactivated
            float brightness = world.getShade(quad.getDirection(), quad.isShade());

            //Default light used when ambientOcclusion is deactivated
            int light = LevelRenderer.getLightColor(world, state, pos);
            blockRenderer.getModelRenderer().calculateShape(world, state, pos, quad.getVertices(), quad.getDirection(), box, flags);

            if (ambientOcclusion) {
                calculator.calculate(world, state, pos, quad.getDirection(), box, flags, quad.isShade());
            }

            //Render the Quad
            SkyLifeRenderUtils.renderQuad(vertexConsumer, matrices, quad, ambientOcclusion, calculator, r, g, b, brightness, light, overlay);
        }
    }

    private static void renderQuad(VertexConsumer vertexConsumer, PoseStack matrices, BakedQuad quad, boolean ambientOcclusion, ModelBlockRenderer.AmbientOcclusionFace calculator, float r, float g, float b, float brightness, int light, int overlay) {
        vertexConsumer.putBulkData(
                matrices.last(), quad, new float[]{
                        ambientOcclusion ? calculator.brightness[0] : brightness,
                        ambientOcclusion ? calculator.brightness[1] : brightness,
                        ambientOcclusion ? calculator.brightness[2] : brightness,
                        ambientOcclusion ? calculator.brightness[3] : brightness,
                }, r, g, b, 1.0F, new int[]{
                        ambientOcclusion ? calculator.lightmap[0] : light,
                        ambientOcclusion ? calculator.lightmap[1] : light,
                        ambientOcclusion ? calculator.lightmap[2] : light,
                        ambientOcclusion ? calculator.lightmap[3] : light,
                }, overlay, true
        );
    }

}
