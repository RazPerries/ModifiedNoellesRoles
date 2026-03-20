package org.agmas.noellesroles.voodoo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.UUID;

public class VoodooPlayerComponent implements AutoSyncedComponent {
    public static final ComponentKey<VoodooPlayerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "voodoo"), VoodooPlayerComponent.class);
    private final PlayerEntity player;
    public UUID target;
    public boolean isTracking = false;
    public boolean wasRefunded = false;
    public int trackingTicks = 0;
    public boolean targetIsKiller = false;

    public void reset() {
        this.target = player.getUuid();
        this.isTracking = false;
        this.trackingTicks = 0;
        this.wasRefunded = false;
        this.targetIsKiller = false;
        this.sync();
    }

    public void silentReset() {
        this.target = player.getUuid();
        this.isTracking = false;
        this.trackingTicks = 0;
        this.wasRefunded = false;
        this.targetIsKiller = false;
        this.sync();
    }

    public VoodooPlayerComponent(PlayerEntity player) {
        this.player = player;
        this.target = player.getUuid();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void setTarget(UUID target) {
        this.target = target;
        this.sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (target == null) target = player.getUuid(); // don't know why i need this null check but it crashes my devenv so whatever i guess
        tag.putUuid("target", target);
        tag.putBoolean("isTracking", isTracking);
        tag.putInt("trackingTicks", trackingTicks);
        tag.putBoolean("wasRefunded", wasRefunded);
        tag.putBoolean("targetIsKiller", targetIsKiller);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.target = tag.contains("target") ? tag.getUuid("target") : player.getUuid();
        this.isTracking = tag.contains("isTracking") && tag.getBoolean("isTracking");
        this.trackingTicks = tag.contains("trackingTicks") ? tag.getInt("trackingTicks") : 0;
        this.wasRefunded = tag.contains("wasRefunded") && tag.getBoolean("wasRefunded");
        this.targetIsKiller = tag.contains("targetIsKiller") && tag.getBoolean("targetIsKiller");
    }
}
