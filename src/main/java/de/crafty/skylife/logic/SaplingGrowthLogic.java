package de.crafty.skylife.logic;

import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SaplingGrowthLogic {

    public static void onSaplingGrowthBySneaking(ServerPlayer player, ServerLevel world) {
        if (!player.isShiftKeyDown())
            return;

        if (SkyLifeConfigs.SAPLING_GROWTH_CONFIG.sneakingWorks())
            SaplingGrowthLogic.applyFastGrowth(world, player.blockPosition(), SkyLifeConfigs.SAPLING_GROWTH_CONFIG.getSneakChance());
    }

    public static void onSaplingGrowthByMoving(ServerPlayer player, ServerLevel world, double prevX, double prevY, double prevZ, double x, double y, double z) {

        if (!SkyLifeConfigs.SAPLING_GROWTH_CONFIG.movingWorks())
            return;

        if (Mth.floor(prevX) != Mth.floor(x) || Math.floor(prevY) != Mth.floor(y) || Math.floor(prevZ) != Mth.floor(z))
            SaplingGrowthLogic.applyFastGrowth(world, player.blockPosition(), SkyLifeConfigs.SAPLING_GROWTH_CONFIG.getMoveChance());
    }


    private static void applyFastGrowth(ServerLevel level, BlockPos src, float f) {

        int workingRadius = SkyLifeConfigs.SAPLING_GROWTH_CONFIG.getWorkingRadius();

        for (int x = -workingRadius; x <= workingRadius; x++) {
            for (int z = -workingRadius; z <= workingRadius; z++) {

                BlockPos pos = new BlockPos(src.getX() + x, src.getY(), src.getZ() + z);

                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof SaplingBlock sapling) || SkyLifeConfigs.SAPLING_GROWTH_CONFIG.getExcludedSaplings().contains(sapling))
                    continue;

                if (!sapling.isBonemealSuccess(level, level.getRandom(), pos, state))
                    return;

                if (level.getRandom().nextFloat() < f) {
                    sapling.performBonemeal(level, level.getRandom(), pos, state);
                    SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.SAPLING_GROWTH, pos, level);
                }
            }


        }
    }

}
