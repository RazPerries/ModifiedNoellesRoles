package org.agmas.noellesroles.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.InstinctBarRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InstinctBarRenderer.class)
public class InstinctBarMixin {

    @ModifyReturnValue(method = "seeInstinctBar", at = @At("RETURN"))
    private static boolean renderBar(boolean original, @NotNull PlayerEntity player) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());

        if ((gameWorldComponent.isRunning() && WatheClient.isPlayerAliveAndInSurvival()) && gameWorldComponent.isRole(player, Noellesroles.CONSPIRATOR)) {
            return true;
        }
        return original;
    }
}
