package org.agmas.noellesroles.mixin.jester;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.item.FirecrackerItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.jester.JesterPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FirecrackerItem.class)
public abstract class JesterFirecrackerUseMixin {

    @Inject(method = "useOnBlock", at = @At(value = "RETURN", ordinal = 0))
    private void jesterUsedCheck (ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.isRole(player, Noellesroles.JESTER)) {
            JesterPlayerComponent jesterPlayerComponent = JesterPlayerComponent.KEY.get(player);
            jesterPlayerComponent.jestCount += jesterPlayerComponent.jestUseFirecracker;
            jesterPlayerComponent.sync();
        }
    }
}