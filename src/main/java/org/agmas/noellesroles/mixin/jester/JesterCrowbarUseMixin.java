package org.agmas.noellesroles.mixin.jester;

import dev.doctor4t.wathe.block_entity.DoorBlockEntity;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.item.CrowbarItem;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.jester.JesterPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrowbarItem.class)
public abstract class JesterCrowbarUseMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void jesterPryCheck(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockEntity entity = world.getBlockEntity(context.getBlockPos());
        if (!(entity instanceof DoorBlockEntity)) entity = world.getBlockEntity(context.getBlockPos().down());
        PlayerEntity player = context.getPlayer();
        if (entity instanceof DoorBlockEntity door && !door.isBlasted() && player != null) {
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
            if (gameWorldComponent.isRole(player, Noellesroles.JESTER)) {
                JesterPlayerComponent jesterPlayerComponent = JesterPlayerComponent.KEY.get(player);
                jesterPlayerComponent.jestCount += jesterPlayerComponent.jestPryDoors;
                jesterPlayerComponent.sync();
            }
        }
    }
}