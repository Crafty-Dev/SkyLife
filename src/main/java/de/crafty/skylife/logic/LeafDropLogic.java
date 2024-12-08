package de.crafty.skylife.logic;

import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LeafDropLogic {


    public static void onLeafBreak(Player player, Level level, BlockPos pos, BlockState state) {
        ItemStack used = player.getMainHandItem();

        if(player.hasInfiniteMaterials())
            return;

        if (!(level instanceof ServerLevel) || !(state.getBlock() instanceof LeavesBlock))
            return;

        if (!(used.getItem() instanceof HoeItem) || EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), used) > 0)
            return;

        ItemStack dropStack = ItemStack.EMPTY;
        int min = SkyLifeConfigs.LEAF_DROP.getMin();
        int max = SkyLifeConfigs.LEAF_DROP.getMax();
        int amount = level.getRandom().nextInt(max - min + 1) + min;

        if (state.is(Blocks.OAK_LEAVES))
            dropStack = new ItemStack(ItemRegistry.OAK_LEAF, amount);

        if (state.is(Blocks.BIRCH_LEAVES))
            dropStack = new ItemStack(ItemRegistry.BIRCH_LEAF, amount);

        if (state.is(Blocks.SPRUCE_LEAVES))
            dropStack = new ItemStack(ItemRegistry.SPRUCE_LEAF, amount);

        if (state.is(Blocks.DARK_OAK_LEAVES))
            dropStack = new ItemStack(ItemRegistry.DARK_OAK_LEAF, amount);

        if (state.is(Blocks.ACACIA_LEAVES))
            dropStack = new ItemStack(ItemRegistry.ACACIA_LEAF, amount);

        if (state.is(Blocks.JUNGLE_LEAVES))
            dropStack = new ItemStack(ItemRegistry.JUNGLE_LEAF, amount);

        if (state.is(Blocks.MANGROVE_LEAVES))
            dropStack = new ItemStack(ItemRegistry.MANGROVE_LEAF, amount);

        if(state.is(Blocks.CHERRY_LEAVES))
            dropStack = new ItemStack(ItemRegistry.CHERRY_LEAF, amount);

        if (!dropStack.isEmpty() && dropStack.getCount() > 0)
            Block.popResource(level, pos, dropStack);
    }

}
