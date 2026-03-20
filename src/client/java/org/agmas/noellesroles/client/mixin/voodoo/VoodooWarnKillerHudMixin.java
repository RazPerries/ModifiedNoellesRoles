package org.agmas.noellesroles.client.mixin.voodoo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class VoodooWarnKillerHudMixin {

    private static float killerWarningAlpha = 0f;

    @Inject(method = "render", at = @At("HEAD"))
    public void renderKillerVoodooWarning(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (NoellesrolesClient.killerWarningTicks > 0) {
            NoellesrolesClient.killerWarningTicks--;
            killerWarningAlpha = Math.min(killerWarningAlpha + 0.1f, 1f);
        } else {
            killerWarningAlpha = Math.max(killerWarningAlpha - 0.05f, 0f);
        }

        if (killerWarningAlpha > 0.05f) {
            Text warningText = Text.translatable("tip.voodoo.warning");
            context.getMatrices().push();
            context.getMatrices().translate((float)context.getScaledWindowWidth() / 2.0F, (float)context.getScaledWindowHeight() * 0.75F, 0.0F);
            context.getMatrices().scale(0.8F, 0.8F, 1.0F);
            int color = MathHelper.packRgb(171/255f, 172/255f, 241/255f) | ((int)(killerWarningAlpha * 255) << 24);
            context.drawTextWithShadow(client.textRenderer, warningText, -client.textRenderer.getWidth(warningText) / 2, 0, color);
            context.getMatrices().pop();
        }
    }
}