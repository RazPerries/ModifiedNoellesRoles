package org.agmas.noellesroles.mixin.morphling;


import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.agmas.noellesroles.bartender.BartenderPlayerComponent;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.agmas.noellesroles.jester.JesterPlayerComponent;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.phantom.PhantomPlayerComponent;
import org.agmas.noellesroles.recaller.RecallerPlayerComponent;
import org.agmas.noellesroles.swapper.SwapperPlayerComponent;
import org.agmas.noellesroles.voodoo.VoodooPlayerComponent;
import org.agmas.noellesroles.vulture.VulturePlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public abstract class MorphlingReseterMixin {

    @Inject(method = "resetPlayer", at = @At("TAIL"))
    private static void jesterWrite(ServerPlayerEntity player, CallbackInfo ci) {
        (MorphlingPlayerComponent.KEY.get(player)).reset();
        (VoodooPlayerComponent.KEY.get(player)).reset();
        (RecallerPlayerComponent.KEY.get(player)).reset();
        (VulturePlayerComponent.KEY.get(player)).reset();
        (ExecutionerPlayerComponent.KEY.get(player)).reset();
        (PhantomPlayerComponent.KEY.get(player)).reset();
        (JesterPlayerComponent.KEY.get(player)).reset();
        (SwapperPlayerComponent.KEY.get(player)).reset();
        (BartenderPlayerComponent.KEY.get(player)).reset();
    }
}
