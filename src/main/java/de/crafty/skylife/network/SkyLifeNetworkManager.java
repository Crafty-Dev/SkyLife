package de.crafty.skylife.network;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.ResourceLocation;

public class SkyLifeNetworkManager {

    //Server to Client

    //Client Events
    public static final ResourceLocation CLIENT_EVENT_PACKET_ID = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "client_update");

    public static void registerPackets(){
        PayloadTypeRegistry.playS2C().register(SkyLifeClientEventPayload.ID, SkyLifeClientEventPayload.CODEC);
    }

}
