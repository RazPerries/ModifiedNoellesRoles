package org.agmas.noellesroles.mixin.iron_will;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerInstinctComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInstinctComponent.class)
public class IronWillInstinctMixin {
    @Shadow @Final private PlayerEntity player;

    @ModifyReturnValue(method = "getMAX_CHARGE", at = @At("RETURN"))
    private float ironWillMaxCharge(float original) {
        WorldModifierComponent modifierComponent = WorldModifierComponent.KEY.get(this.player.getWorld());
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(this.player.getWorld());
        if (modifierComponent.isModifier(player, Noellesroles.IRON_WILLED) && gameWorldComponent.canUseKillerFeatures(this.player)) {
            //Original: 100.0f
            return 125.0f;
        }
        return original;
    }
}
