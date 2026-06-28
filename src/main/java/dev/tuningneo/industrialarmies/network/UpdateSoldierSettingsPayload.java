package dev.tuningneo.industrialarmies.network;

import dev.tuningneo.industrialarmies.IndustrialArmies;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateSoldierSettingsPayload(
        int entityId,
        String name,
        int order,
        int combatStance,
        int followDistance,
        boolean showName
) implements CustomPacketPayload {
    public static final Type<UpdateSoldierSettingsPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(IndustrialArmies.MOD_ID, "update_soldier_settings"));

    public static final StreamCodec<ByteBuf, UpdateSoldierSettingsPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public UpdateSoldierSettingsPayload decode(ByteBuf buffer) {
            return new UpdateSoldierSettingsPayload(
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.STRING_UTF8.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.BOOL.decode(buffer));
        }

        @Override
        public void encode(ByteBuf buffer, UpdateSoldierSettingsPayload payload) {
            ByteBufCodecs.VAR_INT.encode(buffer, payload.entityId());
            ByteBufCodecs.STRING_UTF8.encode(buffer, payload.name());
            ByteBufCodecs.VAR_INT.encode(buffer, payload.order());
            ByteBufCodecs.VAR_INT.encode(buffer, payload.combatStance());
            ByteBufCodecs.VAR_INT.encode(buffer, payload.followDistance());
            ByteBufCodecs.BOOL.encode(buffer, payload.showName());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
