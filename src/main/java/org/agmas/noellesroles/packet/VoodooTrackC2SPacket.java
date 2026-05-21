package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.UUID;

public record VoodooTrackC2SPacket(UUID player) implements CustomPayload {
    public static final Identifier VOODOO_PAYLOAD_ID = Identifier.of(Noellesroles.MOD_ID, "voodoo");
    public static final Id<VoodooTrackC2SPacket> ID = new Id<>(VOODOO_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, VoodooTrackC2SPacket> CODEC;

    public VoodooTrackC2SPacket(UUID player) {
        this.player = player;
    }

    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.player);
    }

    public static VoodooTrackC2SPacket read(PacketByteBuf buf) {
        return new VoodooTrackC2SPacket(buf.readUuid());
    }


    public UUID player() {
        return this.player;
    }


    static {
        CODEC = PacketCodec.of(VoodooTrackC2SPacket::write, VoodooTrackC2SPacket::read);
    }
}