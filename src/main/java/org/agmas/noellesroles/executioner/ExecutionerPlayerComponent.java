package org.agmas.noellesroles.executioner;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ExecutionerPlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public static final ComponentKey<ExecutionerPlayerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "executioner"), ExecutionerPlayerComponent.class);
    private final PlayerEntity player;
    public UUID target;
    public boolean won = false;
    public boolean hasRerolled = false;
    public int gunDropTimer;


    public void reset() {
        this.hasRerolled = false;
        this.target = player.getUuid();
        this.gunDropTimer = -1;
        this.sync();
    }

    public ExecutionerPlayerComponent(PlayerEntity player) {
        this.player = player;
        target = player.getUuid();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void clientTick() {
    }

    public void serverTick() {
        if (this.gunDropTimer > 0 && this.won) {
            this.gunDropTimer--;
        }
        if (this.gunDropTimer == 0){
            if (player.getInventory().contains((itemStack) -> itemStack.isOf(WatheItems.REVOLVER))) {
                this.player.getInventory().remove((s) -> s.isOf(WatheItems.REVOLVER), 1, this.player.getInventory());
                this.player.dropItem(WatheItems.REVOLVER.getDefaultStack(), true, false);
            }
            this.gunDropTimer = -1;
        }

        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        if (!gameWorldComponent.isRole(player, Noellesroles.EXECUTIONER)) return;
        PlayerEntity player1 = player.getWorld().getPlayerByUuid(target);
        if (player1 == null || !gameWorldComponent.getRole(player1).isInnocent() || (GameFunctions.isPlayerEliminated(player1)) && !won) {
            List<UUID> innocentPlayers = new ArrayList<>();
            gameWorldComponent.getRoles().forEach((uuid2,role1)->{
                PlayerEntity player2 = player.getWorld().getPlayerByUuid(uuid2);
                if (uuid2 == null) return;
                if (role1.isInnocent() && GameFunctions.isPlayerAliveAndSurvival(player2) && !role1.equals(WatheRoles.VIGILANTE) && !role1.equals(Noellesroles.MIMIC)) {
                    innocentPlayers.add(uuid2);
                }
            });
            Collections.shuffle(innocentPlayers);
            if (!innocentPlayers.isEmpty()) {
                target = innocentPlayers.getFirst();
            }
        }
        sync();
    }

    public void setGunDropTimer() {
        gunDropTimer = 20 * 20;
        this.player.sendMessage(Text.literal("I cannot hold onto this revolver any longer. I must use it or lose it.").formatted(Formatting.RED), true);
    }

    public void setTarget(UUID target) {
        this.target = target;
        this.sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putUuid("target", this.target);
        tag.putBoolean("hasRerolled", this.hasRerolled);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.target = tag.contains("target") ? tag.getUuid("target") : player.getUuid();
        this.hasRerolled = tag.getBoolean("hasRerolled");
    }
}
