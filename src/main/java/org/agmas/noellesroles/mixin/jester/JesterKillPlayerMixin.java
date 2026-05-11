package org.agmas.noellesroles.mixin.jester;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import org.agmas.noellesroles.jester.JesterPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(GameFunctions.class)
public class JesterKillPlayerMixin {
    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At("HEAD"))
    private static void jesterKillConfirm(PlayerEntity victim, boolean spawnBody, PlayerEntity killer, Identifier identifier, CallbackInfo ci) {
        if (killer != null) {
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(victim.getWorld());
            if (gameWorldComponent.isRole(killer, Noellesroles.JESTER)) {
                JesterPlayerComponent jesterPlayerComponent = JesterPlayerComponent.KEY.get(killer);
                jesterPlayerComponent.jestCount += jesterPlayerComponent.jestKillPlayer;
            }
        }
    }
}
