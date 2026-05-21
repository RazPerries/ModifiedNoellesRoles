package org.agmas.noellesroles.mixin.jester;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.bartender.BartenderPlayerComponent;
import org.agmas.noellesroles.jester.JesterPlayerComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerPoisonComponent.class)
public abstract class JesterDeluPlayerMixin {

    @Shadow @Final private PlayerEntity player;

    @Inject(method = "setPoisonTicks", at = @At("HEAD"))
    private void jesterPoisonCheck(int ticks, @NotNull UUID poisoner, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.isRole(poisoner, Noellesroles.JESTER) && !gameWorldComponent.canUseKillerFeatures(player) && !Noellesroles.KILLER_SIDED_NEUTRALS.contains(gameWorldComponent.getRole(player))) {
            if (player.getWorld().getPlayerByUuid(poisoner) == null) return;
            for (ServerPlayerEntity serverPlayer : this.player.getServer().getPlayerManager().getPlayerList()) {
                if (serverPlayer == null) continue;
                if (gameWorldComponent.isRole(serverPlayer, Noellesroles.JESTER) && GameFunctions.isPlayerAliveAndSurvival(serverPlayer)) {
                    JesterPlayerComponent jesterPlayerComponent = JesterPlayerComponent.KEY.get(serverPlayer);
                    serverPlayer.sendMessage(Text.translatable("tip.jester.delusion_poisoned").withColor(Noellesroles.JESTER.color()), true);
                    jesterPlayerComponent.jestCount += jesterPlayerComponent.jestDelusionPlayer;
                    jesterPlayerComponent.sync();
                }
            }
        }
    }
}