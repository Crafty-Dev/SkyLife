package de.crafty.skylife.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class SkyLifeByteBufCodecs {

    public static <T extends Enum<?>> StreamCodec<ByteBuf, T> createSimpleEnum(Class<T> enumClass){

        return new StreamCodec<>() {
            @Override
            public @NotNull T decode(ByteBuf byteBuf) {
                return enumClass.getEnumConstants()[byteBuf.readByte()];
            }

            @Override
            public void encode(ByteBuf byteBuf, T entry) {
                byteBuf.writeByte(entry.ordinal());
            }
        };
    }

}
