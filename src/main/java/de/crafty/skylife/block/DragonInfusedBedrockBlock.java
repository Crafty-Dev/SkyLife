package de.crafty.skylife.block;

import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DragonInfusedBedrockBlock extends Block {



    public DragonInfusedBedrockBlock() {
        super(Properties.ofFullCopy(Blocks.BEDROCK).lightLevel(value -> 10));
    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(player.getItemInHand(hand).getItem() != Items.NETHERITE_AXE || hit.getDirection() != Direction.UP)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        world.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
        Block.popResource(world, pos.above(), new ItemStack(ItemRegistry.DRAGON_ARTIFACT));
        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.25F, false);
        if(world.isClientSide()){
            world.addDestroyBlockEffect(pos, BlockRegistry.DRAGON_INFUSED_BEDROCK.defaultBlockState());
        }
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        return ItemInteractionResult.SUCCESS;
    }


    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, world, pos, sourceBlock, sourcePos, notify);
    }
}
