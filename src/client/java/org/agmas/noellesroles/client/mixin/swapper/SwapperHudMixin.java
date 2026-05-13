package org.agmas.noellesroles.client.mixin.swapper;

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
import org.agmas.noellesroles.swapper.SwapperPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class SwapperHudMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    public void phantomHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        AbilityPlayerComponent abilityPlayerComponent = AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        SwapperPlayerComponent swapperPlayerComponent = SwapperPlayerComponent.KEY.get(MinecraftClient.getInstance().player);

        if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.SWAPPER)) {
            int drawY = context.getScaledWindowHeight();

            Text line = Text.translatable("tip.swapper", swapperPlayerComponent.swapCost);

            if (abilityPlayerComponent.cooldown > 0) {
                line = Text.translatable("tip.noellesroles.cooldown", abilityPlayerComponent.cooldown/20);
            }

            if (gameWorldComponent.isPsychoActive()){
                line = Text.translatable("tip.noellesroles.global_psycho_ability_disable");
            }

            drawY -= getTextRenderer().getWrappedLinesHeight(line, 999999);
            context.drawTextWithShadow(getTextRenderer(), line, context.getScaledWindowWidth() - getTextRenderer().getWidth(line), drawY, Colors.RED);
        }
    }
}
