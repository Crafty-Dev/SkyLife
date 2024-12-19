package de.crafty.skylife.network.payload;

import de.crafty.skylife.network.SkyLifeByteBufCodecs;
import de.crafty.skylife.network.SkyLifeNetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SkyLifeClientEventPayload(BlockPos pos, ClientEventType eventType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SkyLifeClientEventPayload> ID = new CustomPacketPayload.Type<>(SkyLifeNetworkManager.CLIENT_EVENT_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SkyLifeClientEventPayload> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, SkyLifeClientEventPayload::pos, ClientEventType.STREAM_CODEC, SkyLifeClientEventPayload::eventType, SkyLifeClientEventPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public enum ClientEventType {
        //Misc
        END_PORTAL_DESTROYED,
        SAPLING_GROWTH,

        //Briquette Generator
        BG_BRIQUETTE_CHANGE,
        BG_WORKING_STARTED,
        BG_WORKING_FINISHED,

        //Block Breaker
        BB_BLOCK_HIT,
        BB_ITEM_BREAK,
        BB_HAMMER_BLOCK,

        //Block Melter
        BM_EXSTINGUISH,
        BM_MELTING_FINISHED,
        BM_PLACE_ITEM;

        public static final StreamCodec<ByteBuf, ClientEventType> STREAM_CODEC = SkyLifeByteBufCodecs.createSimpleEnum(ClientEventType.class);
    }
}
