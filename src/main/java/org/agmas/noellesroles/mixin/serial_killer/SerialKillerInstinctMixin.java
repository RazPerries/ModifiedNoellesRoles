package org.agmas.noellesroles.mixin.serial_killer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.doctor4t.wathe.cca.PlayerInstinctComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInstinctComponent.class)
public class SerialKillerInstinctMixin {
    @Shadow
    @Final
    private PlayerEntity player;

    @ModifyReturnValue(method = "getINITIAL_DRAIN", at = @At("RETURN"))
    private float serialKillerNoInitialDrain(float original) {
        WorldModifierComponent modifierComponent = WorldModifierComponent.KEY.get(this.player.getWorld());
        if (modifierComponent.isModifier(player, Noellesroles.SERIAL_KILLER)) {
            return 0;
        }
        return original;
    }

    @ModifyReturnValue(method = "getHOLD_DRAIN_PER_SECOND", at = @At("RETURN"))
    private float serialKillerNoDrain(float original) {
        WorldModifierComponent modifierComponent = WorldModifierComponent.KEY.get(this.player.getWorld());
        if (modifierComponent.isModifier(player, Noellesroles.SERIAL_KILLER)) {
            return 0;
        }
        return original;
    }
}
