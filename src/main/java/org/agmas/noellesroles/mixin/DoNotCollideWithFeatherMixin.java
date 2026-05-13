package org.agmas.noellesroles.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.swapper.SwapperPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(value = Entity.class, priority = 1005)
public abstract class DoNotCollideWithFeatherMixin {


    @WrapMethod(method = "collidesWith")
    boolean doNotCollideWithPlayers(Entity other, Operation<Boolean> original) {
        Entity self = (Entity)(Object)this;
        if (other instanceof PlayerEntity player && self instanceof PlayerEntity player2) {
            WorldModifierComponent worldModifierComponent = WorldModifierComponent.KEY.get(player.getWorld());
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
            if (worldModifierComponent.isModifier(player, Noellesroles.FEATHER) || worldModifierComponent.isModifier(player2, Noellesroles.FEATHER)) {
                return false;
            }
            for (UUID uuid : gameWorldComponent.getAllWithRole(Noellesroles.SWAPPER)) { // Praying this doesn't lag the server
                PlayerEntity swapper = player.getWorld().getPlayerByUuid(uuid);
                if (swapper == null) continue;
                SwapperPlayerComponent swapperPlayerComponent = SwapperPlayerComponent.KEY.get(swapper);
                if ((player == swapperPlayerComponent.getPlayer1() || player == swapperPlayerComponent.getPlayer2() || player2 == swapperPlayerComponent.getPlayer1() || player2 == swapperPlayerComponent.getPlayer2())
                        && swapperPlayerComponent.swapTicks > -1) {
                    return false;
                }
            }
        }
        return original.call(other);
    }
}
