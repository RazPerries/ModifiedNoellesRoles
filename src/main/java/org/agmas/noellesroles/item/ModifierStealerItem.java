package org.agmas.noellesroles.item;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.game.GameConstants;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.coroner.BodyDeathReasonComponent;

import java.awt.*;

public class ModifierStealerItem extends Item {
    public ModifierStealerItem(Settings settings) { super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getItemCooldownManager().isCoolingDown(this)) {
            if (entity instanceof PlayerBodyEntity body) {
                WorldModifierComponent worldModifierComponent = WorldModifierComponent.KEY.get(user.getWorld());
                GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(user.getWorld());
                BodyDeathReasonComponent bodyDeathReasonComponent = BodyDeathReasonComponent.KEY.get(body);

                if (worldModifierComponent.getModifiers(body.getPlayerUuid()).isEmpty() || bodyDeathReasonComponent.vultured) {
                    user.sendMessage(Text.literal("This body does not have a valid modifier to take.").withColor(Color.RED.getRGB()), true);
                    return ActionResult.PASS;
                }

                Modifier stolenModifier = worldModifierComponent.getModifiers(body.getPlayerUuid()).getFirst();
                if (stolenModifier.equals(Noellesroles.TINY)
                        || ((gameWorldComponent.canUseKillerFeatures(user) || Noellesroles.KILLER_SIDED_NEUTRALS.contains(gameWorldComponent.getRole(user)))
                        && (stolenModifier.equals(Noellesroles.BELLRINGER)))) {

                    user.sendMessage(Text.literal("This body does not have a valid modifier to take.").withColor(Color.RED.getRGB()), true);
                    return ActionResult.PASS;
                }

                if (!worldModifierComponent.getModifiers(user.getUuid()).contains(stolenModifier)) {
                    worldModifierComponent.addModifier(user.getUuid(), stolenModifier);
                    if (stolenModifier.equals(Noellesroles.FEATHER)) {
                        user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, StatusEffectInstance.INFINITE, 0, true, false));
                    }
                    if (!user.getWorld().isClient) {
                        user.getWorld().playSound(null, body.getX(), body.getY() + .1f, body.getZ(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.PLAYERS, 0.5f, 1f + user.getWorld().random.nextFloat() * .1f - .05f);
                        user.sendMessage(Text.literal("Took the modifier ").append(StringUtil.capitalize(stolenModifier.identifier().getPath()).replace("_", " ")).withColor(stolenModifier.color()), true);
                    }
                    if (!user.isCreative()) {
                        user.getStackInHand(hand).decrement(1);
                        user.getItemCooldownManager().set(this, GameConstants.ITEM_COOLDOWNS.get(this));
                    }
                    return ActionResult.SUCCESS;

                } else {user.sendMessage(Text.literal("You already have this modifier.").withColor(Color.RED.getRGB()), true);}
            }
        }
        return ActionResult.PASS;
    }
}