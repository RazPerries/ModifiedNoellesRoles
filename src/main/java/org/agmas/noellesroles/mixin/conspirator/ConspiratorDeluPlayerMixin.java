package org.agmas.noellesroles.mixin.conspirator;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.jester.JesterPlayerComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerPoisonComponent.class)
public abstract class ConspiratorDeluPlayerMixin  {

    @Shadow
    @Final
    private PlayerEntity player;

    @Inject(method = "setPoisonTicks", at = @At("HEAD"), cancellable = true)
    private void conspiratorPoisonCheck(int ticks, @NotNull UUID poisoner, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.isRole(poisoner, Noellesroles.CONSPIRATOR) && !gameWorldComponent.canUseKillerFeatures(player) && !Noellesroles.KILLER_SIDED_NEUTRALS.contains(gameWorldComponent.getRole(player))) {
            if (player.getWorld().getPlayerByUuid(poisoner) == null) return;
            for (ServerPlayerEntity serverPlayer : this.player.getServer().getPlayerManager().getPlayerList()) {
                if (serverPlayer == null) continue;
                if (gameWorldComponent.isRole(serverPlayer, Noellesroles.CONSPIRATOR) && GameFunctions.isPlayerAliveAndSurvival(serverPlayer)) {
                    PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(serverPlayer);
                    serverPlayer.sendMessage(Text.translatable("tip.conspirator.delusion_reimbursement").withColor(Colors.LIGHT_GRAY), true);
                    playerShopComponent.addToBalance(60);
                    ci.cancel();
                }
            }
        }
    }
}

