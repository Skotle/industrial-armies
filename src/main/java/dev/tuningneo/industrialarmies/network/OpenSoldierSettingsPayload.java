package dev.tuningneo.industrialarmies.network;

import dev.tuningneo.industrialarmies.IndustrialArmies;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenSoldierSettingsPayload(int entityId) implements CustomPacketPayload {
    public static final Type<OpenSoldierSettingsPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(IndustrialArmies.MOD_ID, "open_soldier_settings"));
    public static final StreamCodec<ByteBuf, OpenSoldierSettingsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            OpenSoldierSettingsPayload::entityId,
            OpenSoldierSettingsPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
