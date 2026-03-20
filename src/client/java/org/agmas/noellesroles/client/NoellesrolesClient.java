package org.agmas.noellesroles.client;

import com.google.common.collect.Maps;
import dev.doctor4t.ratatouille.util.TextUtils;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.util.WatheItemTooltips;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.index.WatheItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.NoellesRolesEntities;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.renderer.RoleMineEntityRenderer;
import org.agmas.noellesroles.client.renderer.ShortFuseFirecrackerEntityRenderer;
import org.agmas.noellesroles.packet.AbilityC2SPacket;
import org.agmas.noellesroles.packet.MorphC2SPacket;
import org.agmas.noellesroles.packet.VoodooWarnKillerS2CPacket;
import org.agmas.noellesroles.packet.VultureEatC2SPacket;
import org.agmas.noellesroles.voodoo.VoodooPlayerComponent;
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.Entity;

import java.util.*;

public class NoellesrolesClient implements ClientModInitializer {


    public static int insanityTime = 0;
    public static int killerWarningTicks = 0;
    public static KeyBinding abilityBind;
    public static PlayerEntity target;
    public static PlayerBodyEntity targetBody;
    public static PlayerEntity lookingAt = null;

    public static Map<UUID, UUID> SHUFFLED_PLAYER_ENTRIES_CACHE = Maps.newHashMap();


    @Override
    public void onInitializeClient() {
        abilityBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + Noellesroles.MOD_ID + ".ability", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.wathe.keybinds"));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            insanityTime++;
            if (insanityTime >= 20*6) {
                insanityTime = 0;
                List<UUID> keys = new ArrayList<UUID>(WatheClient.PLAYER_ENTRIES_CACHE.keySet());
                List<UUID> originalkeys = new ArrayList<UUID>(WatheClient.PLAYER_ENTRIES_CACHE.keySet());
                Collections.shuffle(keys);
                int i = 0;
                for (UUID o : originalkeys) {
                    SHUFFLED_PLAYER_ENTRIES_CACHE.put(o, keys.get(i));
                    i++;
                }
            }
            if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) client.crosshairTarget).getEntity();
                if (entity instanceof PlayerEntity player && player != client.player && client.player.squaredDistanceTo(player) <= 2.25 * 2.25) {
                    lookingAt = player;
                } else {
                    lookingAt = null;
                }
            } else {
                lookingAt = null;
            }
            if (abilityBind.wasPressed()) {
                PacketByteBuf data = PacketByteBufs.create();
                client.execute(() -> {
                    if (MinecraftClient.getInstance().player == null) return;
                    GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
                    if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.VOODOO)) {
                        VoodooPlayerComponent voodoo = VoodooPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
                        if (voodoo.target.equals(MinecraftClient.getInstance().player.getUuid())) {
                            if (lookingAt != null) {
                                ClientPlayNetworking.send(new MorphC2SPacket(lookingAt.getUuid()));
                            }
                        } else {
                            ClientPlayNetworking.send(new AbilityC2SPacket());
                        }
                        return;
                    }
                    if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.VULTURE)) {
                        if (targetBody == null) return;
                        ClientPlayNetworking.send(new VultureEatC2SPacket(targetBody.getUuid()));
                        return;
                    }
                    ClientPlayNetworking.send(new AbilityC2SPacket());
                });
            }
        });
        EntityRendererRegistry.register(NoellesRolesEntities.ROLE_MINE_ENTITY_ENTITY_TYPE, RoleMineEntityRenderer::new);
        EntityRendererRegistry.register(NoellesRolesEntities.SHORTFUSE_FIRECRACKER_ENTITY_ENTITY_TYPE, ShortFuseFirecrackerEntityRenderer::new);

        ItemTooltipCallback.EVENT.register(((itemStack, tooltipContext, tooltipType, list) -> {
            tooltipHelper(ModItems.DEFENSE_VIAL, itemStack, list);
            tooltipHelper(ModItems.ROLE_MINE, itemStack, list);
            tooltipHelper(ModItems.DELUSION_VIAL, itemStack, list);
            tooltipHelper(ModItems.SHORTFUSE_FIRECRACKER, itemStack, list);
        }));

        ClientPlayNetworking.registerGlobalReceiver(VoodooWarnKillerS2CPacket.ID, (payload, context) -> {
            killerWarningTicks = 20 * 15;
        });
    }

    public void tooltipHelper(Item item, ItemStack itemStack, List<Text> list) {
        if (itemStack.isOf(item)) {
            list.addAll(TextUtils.getTooltipForItem(item, Style.EMPTY.withColor(WatheItemTooltips.REGULAR_TOOLTIP_COLOR)));
        }
    }
}
