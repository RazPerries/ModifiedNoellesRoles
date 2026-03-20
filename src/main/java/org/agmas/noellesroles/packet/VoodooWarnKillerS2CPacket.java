package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

public record VoodooWarnKillerS2CPacket() implements CustomPayload {
    public static final Identifier VOODOO_WARN_KILLER_PAYLOAD_ID = Identifier.of(Noellesroles.MOD_ID, "voodoo_warn_killer");
    public static final Id<VoodooWarnKillerS2CPacket> ID = new Id<>(VOODOO_WARN_KILLER_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, VoodooWarnKillerS2CPacket> CODEC;

    public VoodooWarnKillerS2CPacket() {
    }

    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void write(PacketByteBuf buf) {
    }

    public static VoodooWarnKillerS2CPacket read(PacketByteBuf buf) {
        return new VoodooWarnKillerS2CPacket();
    }

    static {
        CODEC = PacketCodec.of(VoodooWarnKillerS2CPacket::write, VoodooWarnKillerS2CPacket::read);
    }
}