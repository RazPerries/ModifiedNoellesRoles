package org.agmas.noellesroles;

import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Unique;

public class ShopComponent {
    public static void failedPurchase(PlayerEntity buyer){
        buyer.sendMessage(Text.literal("Purchase Failed").formatted(Formatting.DARK_RED), true);
        if (buyer instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) buyer;
            player.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(WatheSounds.UI_SHOP_BUY_FAIL), SoundCategory.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 0.9F + buyer.getRandom().nextFloat() * 0.2F, player.getRandom().nextLong()));
        }
    }
}