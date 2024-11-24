package de.crafty.skylife.logic;

import de.crafty.skylife.config.HammerConfig;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.item.HammerItem;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HammerLogic {

    private static final LinkedList<Tier> TIER_ORDER = new LinkedList<>(List.of(
            Tiers.WOOD,
            Tiers.STONE,
            Tiers.IRON,
            Tiers.GOLD,
            Tiers.DIAMOND,
            Tiers.NETHERITE
    ));

    public static void onBlockHammering(Player player, Level level, BlockPos blockPos, BlockState state){

        ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (!(heldStack.getItem() instanceof HammerItem hammer))
            return;

        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer))
            return;

        if (!isHammerable(state.getBlock()))
            return;


        HammerLogic.handleEffects(serverLevel, serverPlayer, blockPos, state);

        for (ItemStack stack : HammerLogic.getRandomDrop(state.getBlock(), serverLevel)) {
            if(HammerLogic.isGoodHammer(hammer)){
                ItemEntity item = new ItemEntity(serverLevel, blockPos.getX() + 0.5D, blockPos.getY() + 0.125D, blockPos.getZ() + 0.5D, stack);
                item.setDeltaMovement(0.0D, 0.0D, 0.0D);
                serverLevel.addFreshEntity(item);
                continue;
            }

            Block.popResource(level, blockPos, stack);
        }

    }

    private static boolean isGoodHammer(HammerItem item){
        return TIER_ORDER.indexOf(item.getTier()) >= TIER_ORDER.indexOf(SkyLifeConfigs.HAMMER.getPrecisionDropTier());
    }

    private static void handleEffects(ServerLevel serverWorld, ServerPlayer serverPlayer, BlockPos blockPos, BlockState state){
        serverPlayer.playNotifySound(state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5F, 0.5F);

        int xParticles = 3;
        int yParticles = 3;
        int zParticles = 3;

        for (int x = 0; x < xParticles; x++) {
            for (int y = 0; y < yParticles; y++) {
                for (int z = 0; z < zParticles; z++) {
                    serverWorld.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), blockPos.getX() + (x * (1.0F / (xParticles - 1))), blockPos.getY() + (y * (1.0F / (yParticles - 1))), blockPos.getZ() + (z * (1.0F / (zParticles - 1))), 2, 0.0D, 0.0D, 0.0D, 1.0D);
                }
            }
        }
    }


    public static List<ItemStack> getRandomDrop(Block block, ServerLevel world) {

        List<HammerConfig.HammerDrop> availableDrops = SkyLifeConfigs.HAMMER.getDrops().getOrDefault(block, List.of());

        List<ItemStack> drops = new ArrayList<>();

        for (HammerConfig.HammerDrop drop : availableDrops) {

            if (world.getRandom().nextFloat() >= drop.chance())
                continue;

            int count = drop.min();

            for (int i = drop.min(); i < drop.max(); i++) {
                if (world.getRandom().nextFloat() < drop.bonusChance())
                    count++;
            }
            drops.add(new ItemStack(drop.item(), count));
        }

        return drops;
    }

    public static boolean isHammerable(Block block) {
        return SkyLifeConfigs.HAMMER.getDrops().containsKey(block);
    }

}
