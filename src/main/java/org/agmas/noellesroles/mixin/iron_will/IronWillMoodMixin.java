package org.agmas.noellesroles.mixin.iron_will;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerMoodComponent.class)
public abstract class IronWillMoodMixin {

    @Shadow @Final private PlayerEntity player;
    @Shadow public abstract float getMood();

    @ModifyVariable(method = "setMood", at = @At("HEAD"), argsOnly = true)
    private float noellesroles$halveMoodDrain(float newMoodValue) {
        WorldModifierComponent modifierComponent = WorldModifierComponent.KEY.get(this.player.getWorld());
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(this.player.getWorld());
        if (modifierComponent.isModifier(this.player, Noellesroles.IRON_WILLED) && gameWorldComponent.isInnocent(this.player)) {
            float currentMood = getMood();
            float drain = currentMood - newMoodValue;
            if (drain > 0) {return currentMood - (drain * 0.5f);}
        }
        return newMoodValue;
    }
}