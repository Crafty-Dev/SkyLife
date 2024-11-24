package de.crafty.skylife.logic;

import de.crafty.skylife.blockentities.GraveStoneBlockEntity;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import java.util.ArrayList;
import java.util.List;

public class GraveStoneLogic {

    public static void onPlayerDeath(ServerPlayer player){
        if(player.getInventory().isEmpty())
            return;

        ServerLevel level = player.serverLevel();
        BlockPos pos = new BlockPos(player.getBlockX(), Math.max(player.getBlockY(), level.getMinBuildHeight() + 1), player.getBlockZ());

        level.setBlock(pos, BlockRegistry.GRAVE_STONE.defaultBlockState(), 3);
        if(level.getBlockState(pos.below()).isAir() || level.getBlockState(pos.below()).liquid())
            level.setBlock(pos.below(), Blocks.DIRT.defaultBlockState(), 3);

        if(!(level.getBlockEntity(pos) instanceof GraveStoneBlockEntity blockEntity))
            return;


        List<ItemStack> items = new ArrayList<>();
        player.getInventory().items.stream().filter(stack -> !stack.isEmpty()).forEach(items::add);
        player.getInventory().armor.stream().filter(stack -> !stack.isEmpty()).forEach(items::add);
        player.getInventory().offhand.stream().filter(stack -> !stack.isEmpty()).forEach(items::add);

        blockEntity.setContent(items);
        blockEntity.setPlayerProfile(player.getGameProfile());
        if(!level.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get())
            player.getInventory().clearContent();

        player.playNotifySound(SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

}
