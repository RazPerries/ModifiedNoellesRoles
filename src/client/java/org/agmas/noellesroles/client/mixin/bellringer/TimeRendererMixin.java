package org.agmas.noellesroles.client.mixin.bellringer;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.TimeRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.bellringer.BellringerTimeComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TimeRenderer.class)
public class TimeRendererMixin {

    @Shadow
    public static TimeRenderer.TimeNumberRenderer view;

    @Shadow
    public static float offsetDelta;

    @Inject(method = "renderHud", at = @At("HEAD"), cancellable = true)
    private static void renderBellringerHud(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, float delta, CallbackInfo ci) {
        WorldModifierComponent modifierComp = WorldModifierComponent.KEY.get(player.getWorld());

        if (modifierComp.isModifier(player, Noellesroles.BELLRINGER) && !player.isCreative() && !player.isSpectator()) {
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());

            if (gameWorldComponent.isRunning()) {
                int elapsed = BellringerTimeComponent.KEY.get(player.getWorld()).getElapsedTicks();
                int time = 12000 - elapsed;
                boolean isNegative = time < 0;
                int displayTime = Math.abs(time);

                if (Math.abs(view.getTarget() - (float)displayTime) > 10) {
                    offsetDelta = (float)displayTime > view.getTarget() ? .6f : -.6f;
                }

                offsetDelta = MathHelper.lerp(delta / 16, offsetDelta, 0f);
                view.setTarget((float)displayTime);

                int colour = isNegative ? 0xFFFF5555 : 0xFFFFFFFF;

                context.getMatrices().push();
                context.getMatrices().translate(context.getScaledWindowWidth() / 2f, 6, 0);

                if (isNegative) {
                    context.drawTextWithShadow(renderer, "-", -26, 0, colour);
                }

                view.render(renderer, context, 0, 0, colour, delta);
                context.getMatrices().pop();
            }

            ci.cancel();
        }
    }
}