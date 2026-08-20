package org.agmas.noellesroles.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.cca.PlayerStaminaComponent;
import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerStaminaComponent.class)
public class HealthyMixin {
    @Shadow
    @Final
    private PlayerEntity player;

    @ModifyReturnValue(method = "getAdditionalStamina", at = @At("RETURN"))
    private float healthyStamina(float original) {
        WorldModifierComponent modifierComponent = WorldModifierComponent.KEY.get(this.player.getWorld());
        if (modifierComponent.isModifier(player, Noellesroles.HEALTHY)) {
            return GameConstants.getInTicks(0, 10);
        }
        return original;
    }

}