package org.agmas.noellesroles.client.mixin.voodoo;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.voodoo.VoodooPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class)
public abstract class VoodooHudMixin {
    private static String lastLookingAtName = "";
    private static String savedTargetName = "your target";
    private static UUID lastTarget = null;

    private static int refundDisplayTicks = 0;
    private static float refundAlpha = 0f;
    private static float nameAlpha = 0f;

    @Inject(method = "render", at = @At("HEAD"))
    public void renderVoodooHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(client.player.getWorld());
        if (!gameWorld.isRole(client.player, Noellesroles.VOODOO)) return;

        VoodooPlayerComponent voodoo = VoodooPlayerComponent.KEY.get(client.player);
        AbilityPlayerComponent abilityPlayerComponent = AbilityPlayerComponent.KEY.get(client.player);
        PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(client.player);
        boolean isDepressed = PlayerMoodComponent.KEY.get(client.player).isLowerThanDepressed();

        if (lastTarget != null && !lastTarget.equals(client.player.getUuid())) {
            PlayerEntity targetPlayer = client.world.getPlayerByUuid(lastTarget);
            if (targetPlayer != null) {
                MorphlingPlayerComponent morphling = MorphlingPlayerComponent.KEY.get(targetPlayer);
                if (morphling.getMorphTicks() > 0 && morphling.disguise != null) {
                    PlayerEntity disguisedAs = client.world.getPlayerByUuid(morphling.disguise);
                    savedTargetName = disguisedAs != null ? disguisedAs.getName().getString() : targetPlayer.getName().getString();
                } else {
                    savedTargetName = targetPlayer.getName().getString();
                }
            }
        }

        if (lastTarget != null && !lastTarget.equals(client.player.getUuid()) && voodoo.target.equals(client.player.getUuid()) && !voodoo.isTracking && voodoo.wasRefunded) {
            refundDisplayTicks = 20 * 15;
        }
        lastTarget = voodoo.target;

        Text obfuscatedName = Text.literal("???").styled(s -> s.withFormatting(Formatting.OBFUSCATED));

        Text line;
        if (voodoo.isTracking) {
            if (isDepressed) {
                line = Text.empty()
                        .append(Text.literal("???").styled(s -> s.withFormatting(Formatting.OBFUSCATED)))
                        .append(Text.literal(" has been revealed for the next " + (400 - voodoo.trackingTicks) / 20 + "s."));
            } else {
                line = Text.translatable("tip.voodoo.tracking", savedTargetName, (400 - voodoo.trackingTicks) / 20);
            }
        } else if (abilityPlayerComponent.cooldown > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityPlayerComponent.cooldown / 20);
        } else if (voodoo.target.equals(client.player.getUuid())) {
            if (playerShopComponent.balance < 200) {
                line = Text.translatable("tip.voodoo.not_enough_money");
            } else {
                line = Text.translatable("tip.voodoo.select_target", NoellesrolesClient.abilityBind.getBoundKeyLocalizedText());
            }
        } else {
            if (isDepressed) {
                line = Text.literal("Press ").append(NoellesrolesClient.abilityBind.getBoundKeyLocalizedText()).append(Text.literal(" to reveal ")).append(obfuscatedName).append(Text.literal("."));
            } else {
                line = Text.translatable("tip.voodoo.activate", NoellesrolesClient.abilityBind.getBoundKeyLocalizedText(), savedTargetName);
            }
        }

        if (refundDisplayTicks > 0) {
            refundDisplayTicks--;
            refundAlpha = Math.min(refundAlpha + 0.1f, 1f);
        } else {
            refundAlpha = Math.max(refundAlpha - 0.05f, 0f);
        }

        if (refundAlpha > 0.05f) {
            Text refundText = isDepressed
                    ? Text.literal("You try to track ").append(obfuscatedName).append(Text.literal(", but it requires too much power."))
                    : Text.translatable("tip.voodoo.refund", savedTargetName);
            context.getMatrices().push();
            context.getMatrices().translate((float)context.getScaledWindowWidth() / 2.0F, (float)context.getScaledWindowHeight() * 0.75F, 0.0F);
            context.getMatrices().scale(0.8F, 0.8F, 1.0F);
            int color = MathHelper.packRgb(171/255f, 172/255f, 241/255f) | ((int)(refundAlpha * 255) << 24);
            context.drawTextWithShadow(client.textRenderer, refundText, -client.textRenderer.getWidth(refundText) / 2, 0, color);
            context.getMatrices().pop();
        }

        int drawY = context.getScaledWindowHeight();
        drawY -= client.textRenderer.getWrappedLinesHeight(line, 999999);
        context.drawTextWithShadow(client.textRenderer, line, context.getScaledWindowWidth() - client.textRenderer.getWidth(line), drawY, Noellesroles.VOODOO.color());

        if (NoellesrolesClient.lookingAt != null && !NoellesrolesClient.lookingAt.isInvisible() && GameFunctions.isPlayerAliveAndSurvival(client.player)) {
            MorphlingPlayerComponent morphling = MorphlingPlayerComponent.KEY.get(NoellesrolesClient.lookingAt);
            if (morphling.getMorphTicks() > 0 && morphling.disguise != null) {
                PlayerEntity disguisedAs = client.world.getPlayerByUuid(morphling.disguise);
                lastLookingAtName = disguisedAs != null ? disguisedAs.getName().getString() : NoellesrolesClient.lookingAt.getName().getString();
            } else {
                lastLookingAtName = NoellesrolesClient.lookingAt.getName().getString();
            }
            nameAlpha = Math.min(nameAlpha + 0.1f, 1f);
        } else {
            nameAlpha = Math.max(nameAlpha - 0.05f, 0f);
        }

        if (nameAlpha > 0.05f) {
            context.getMatrices().push();
            context.getMatrices().translate((float)context.getScaledWindowWidth() / 2.0F, (float)context.getScaledWindowHeight() / 2.0F + 6.0F, 0.0F);
            context.getMatrices().scale(0.6F, 0.6F, 1.0F);

            Text text;
            if (voodoo.target.equals(client.player.getUuid())) {
                Text nameDisplay = isDepressed ? obfuscatedName : Text.literal(lastLookingAtName);
                text = Text.literal("Press ").append(NoellesrolesClient.abilityBind.getBoundKeyLocalizedText()).append(Text.literal(" to track ")).append(nameDisplay);
            } else {
                text = Text.literal("You already have a target!");
            }

            int nameColor = MathHelper.packRgb(171/255f, 172/255f, 241/255f) | ((int)(nameAlpha * 255) << 24);
            context.drawTextWithShadow(client.textRenderer, text, -client.textRenderer.getWidth(text) / 2, 32, nameColor);
            context.getMatrices().pop();
        }
    }
}