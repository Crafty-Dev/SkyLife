package de.crafty.skylife.network;

import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

public class SkyLifeNetworkServer {

    public static void registerServerReceivers(){

    }

    public static void sendToPlayer(CustomPacketPayload payload, ServerPlayer player){
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendTracking(ServerLevel level, BlockPos pos, CustomPacketPayload payload){
        PlayerLookup.tracking(level, pos).forEach(serverPlayer -> ServerPlayNetworking.send(serverPlayer, payload));
    }

    public static void sendUpdate(SkyLifeClientEventPayload.ClientEventType type, BlockPos pos, LevelAccessor level) {
        if(level instanceof ServerLevel serverLevel){
            SkyLifeNetworkServer.sendTracking(serverLevel, pos, new SkyLifeClientEventPayload(pos, type));
            return;
        }

        //handleEventPayload(level, pos, type);
    }
}
