package org.agmas.noellesroles.mixin.serial_killer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.cca.PlayerStaminaComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerStaminaComponent.class)
public class SerialKillerStaminaMixin {
    @Shadow
    @Final
    private PlayerEntity player;

    @ModifyReturnValue(method = "getStaminaRegeneration", at = @At("RETURN"))
    private float serialKillerStaminaRegen(float original) {
        WorldModifierComponent modifierComponent = WorldModifierComponent.KEY.get(this.player.getWorld());
        if (modifierComponent.isModifier(player, Noellesroles.SERIAL_KILLER)) {
            return 0.5f;
        }
        return original;
    }
}