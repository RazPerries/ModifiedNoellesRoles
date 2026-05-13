package org.agmas.noellesroles.client.mixin;

import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoundTextRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.util.AnnounceWelcomePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.bartender.BartenderPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(WatheClient.class)
public abstract class InstinctMixin {


    @Shadow public static KeyBinding instinctKeybind;

    @Inject(method = "isInstinctEnabled", at = @At("HEAD"), cancellable = true)
    private static void b(CallbackInfoReturnable<Boolean> cir) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.CONSPIRATOR)) {
            if (instinctKeybind.isPressed()) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "getInstinctHighlight", at = @At("HEAD"), cancellable = true)
    private static void getInstinctHighlightColor(Entity target, CallbackInfoReturnable<Integer> cir) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        WorldModifierComponent worldModifierComponent = WorldModifierComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (worldModifierComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.SIXTH_SENSE) && !MinecraftClient.getInstance().player.isSpectator()) {
            if (target instanceof ItemEntity) {
                if (MinecraftClient.getInstance().player.squaredDistanceTo(target) <= (8.0 * 5.0)) {
                    cir.setReturnValue(Color.ORANGE.getRGB());
                    return;
                }
            }
        }
        if (target instanceof PlayerEntity) {
            if (!((PlayerEntity)target).isSpectator()) {
                BartenderPlayerComponent bartenderPlayerComponent = BartenderPlayerComponent.KEY.get((PlayerEntity) target);
                PlayerPoisonComponent playerPoisonComponent =  PlayerPoisonComponent.KEY.get((PlayerEntity) target);
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.BARTENDER) && bartenderPlayerComponent.glowTicks > 0) {
                    cir.setReturnValue(Color.GREEN.getRGB());
                }
            }
        }
        if (target instanceof PlayerEntity) {
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.EXECUTIONER)) {
                ExecutionerPlayerComponent executionerPlayerComponent = (ExecutionerPlayerComponent) ExecutionerPlayerComponent.KEY.get((PlayerEntity) MinecraftClient.getInstance().player);
                if (executionerPlayerComponent.target.equals(target.getUuid())) {
                    cir.setReturnValue(Color.YELLOW.getRGB());
                    cir.cancel();
                }
            }
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.CONSPIRATOR) && WatheClient.isInstinctEnabled()) {
                double distance = MinecraftClient.getInstance().player.squaredDistanceTo(target);
                if ((gameWorldComponent.canUseKillerFeatures((PlayerEntity) target) || Noellesroles.KILLER_SIDED_NEUTRALS.contains(gameWorldComponent.getRole((PlayerEntity) target)))) {
                    if (distance <= 30 * 30){
                        cir.setReturnValue(Color.RED.getRGB());
                    } else {
                        cir.setReturnValue(-1);
                    }
                    cir.cancel();
                } else {
                    if (distance <= 15 * 15){
                        cir.setReturnValue(Color.GREEN.getRGB());
                    } else {
                        cir.setReturnValue(-1);
                    }
                    cir.cancel();
                }
            }
            if (!((PlayerEntity)target).isSpectator() && WatheClient.isInstinctEnabled()) {
                Role role = gameWorldComponent.getRole((PlayerEntity) target);
                if (role != null) {
                    if (WatheClient.isKiller() && WatheClient.isPlayerAliveAndInSurvival()) {
                        if (Noellesroles.KILLER_SIDED_NEUTRALS.contains(role)) {
                            cir.setReturnValue(new Color(255, 90, 40).getRGB());
                            cir.cancel();
                        } else if (!role.isInnocent() && !role.canUseKiller()) {
                           cir.setReturnValue(5168437);
                           cir.cancel();
                        }
                    }
                }
            }
            if (!((PlayerEntity)target).isSpectator() && WatheClient.isInstinctEnabled()) {
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.EXECUTIONER) && WatheClient.isPlayerAliveAndInSurvival()) {
                    cir.setReturnValue(Noellesroles.EXECUTIONER.color());
                    cir.cancel();
                }
            }
        }
    }
}
