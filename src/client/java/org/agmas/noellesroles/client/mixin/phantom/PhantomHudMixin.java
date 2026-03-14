package org.agmas.noellesroles.client.mixin.phantom;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.phantom.PhantomPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class PhantomHudMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    public void phantomHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        PhantomPlayerComponent phantomPlayerComponent = PhantomPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        PlayerPsychoComponent playerPsychoComponent = PlayerPsychoComponent.KEY.get(MinecraftClient.getInstance().player);

        if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.PHANTOM)) {
            int drawY = context.getScaledWindowHeight();

            //Invisibility ability cooldown (in seconds). Subtract from the ability uptime.
            int abilityCooldown = phantomPlayerComponent.invisCooldown - phantomPlayerComponent.invisTimer;

            Text line = Text.translatable("tip.phantom", NoellesrolesClient.abilityBind.getBoundKeyLocalizedText(), phantomPlayerComponent.invisCount);

            if (phantomPlayerComponent.invisCount == 0){
                line = Text.translatable("tip.noellesroles.ability_used");
            }
            // NOTE: The ability cooldown, uptime, and charges are all handled in the PhantomPlayerComponent.java file. See there to modify the phantom.
            if (abilityCooldown <= abilityPlayerComponent.cooldown/20)  {
                line = Text.translatable("tip.phantom.invisibility", (abilityPlayerComponent.cooldown/20) - abilityCooldown);
            } else if (abilityPlayerComponent.cooldown > 0) {
                line = Text.translatable("tip.noellesroles.cooldown", abilityPlayerComponent.cooldown/20);
            }
            if (playerPsychoComponent.psychoTicks > 0){
                line = Text.translatable("tip.noellesroles.phsycho_ability_disable");
            }

            drawY -= getTextRenderer().getWrappedLinesHeight(line, 999999);
            context.drawTextWithShadow(getTextRenderer(), line, context.getScaledWindowWidth() - getTextRenderer().getWidth(line), drawY, Colors.RED);
        }
    }
}
