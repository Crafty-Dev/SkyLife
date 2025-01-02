package de.crafty.skylife.logic;

import de.crafty.skylife.config.BlockTransformationConfig;
import de.crafty.skylife.config.SkyLifeConfigs;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class BlockTransformationLogic {


    public static InteractionResult onBlockTransformation(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {


        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);

        List<BlockTransformationConfig.BlockTransformation> transformations = SkyLifeConfigs.BLOCK_TRANSFORMATION.getTransformations().get(stack.getItem());
        if (transformations == null)
            return InteractionResult.PASS;

        for (BlockTransformationConfig.BlockTransformation transformation : transformations) {

            if (transformation.block() != state.getBlock())
                continue;

            for (BlockTransformationConfig.TransformCondition condition : transformation.conditions()) {
                if (!condition.check(player, hand, level, state, hitResult.getDirection()))
                    return InteractionResult.PASS;
            }

            level.setBlockAndUpdate(pos, transformation.result());
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), transformation.sound(), SoundSource.BLOCKS, 1.0F, 1.0F, false);

            if (transformation.remainder() != ItemStack.EMPTY)
                player.setItemInHand(hand, transformation.remainder().copy());
            else if (!player.hasInfiniteMaterials())
                stack.consume(1, player);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;

    }


}
