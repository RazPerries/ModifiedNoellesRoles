package org.agmas.noellesroles.client.mixin.voodoo;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.voodoo.VoodooPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class VoodooHudMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    public void phantomHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        AbilityPlayerComponent abilityPlayerComponent = AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        VoodooPlayerComponent voodooPlayerComponent = VoodooPlayerComponent.KEY.get(MinecraftClient.getInstance().player);

        if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.VOODOO)) {
            int drawY = context.getScaledWindowHeight();

            Text line = Text.translatable("tip.voodoo.select_target", NoellesrolesClient.abilityBind.getBoundKeyLocalizedText(), voodooPlayerComponent.abilityCost);

            if (voodooPlayerComponent.hasTarget) {
                line = Text.translatable("tip.voodoo.target_tracked", NoellesrolesClient.abilityBind.getBoundKeyLocalizedText());
            }

            if (abilityPlayerComponent.cooldown > 0) {
                line = Text.translatable("tip.noellesroles.cooldown", abilityPlayerComponent.cooldown/20);
            }

            if (voodooPlayerComponent.glowTicks > 0) {
                line = Text.translatable("tip.voodoo.track_timer", voodooPlayerComponent.glowTicks/20);
            }

            drawY -= getTextRenderer().getWrappedLinesHeight(line, 999999);
            context.drawTextWithShadow(getTextRenderer(), line, context.getScaledWindowWidth() - getTextRenderer().getWidth(line), drawY, Noellesroles.VOODOO.color());
        }
    }
}
