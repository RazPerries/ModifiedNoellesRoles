package org.agmas.noellesroles.voodoo;

import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public class VoodooPlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public static final ComponentKey<VoodooPlayerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "voodoo"), VoodooPlayerComponent.class);
    private final PlayerEntity player;
    public UUID target;

    public int glowTicks = -1;
    public int abilityCost = 200;
    public boolean hasTarget = false;

    public void reset() {
        this.target = null;
        this.hasTarget = false;
        this.glowTicks = -1;
        this.sync();
    }

    public void clientTick() {
    }

    public void serverTick() {
        if (this.glowTicks > 0 && this.hasTarget) {
            --this.glowTicks;
        }
        if (this.glowTicks == 0) {
            this.hasTarget = false;
            this.glowTicks = -1;
            this.target = null;
        }
        this.sync();
    }

    public VoodooPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void startGlow() {
        this.glowTicks = (GameConstants.getInTicks(0,30));
        this.sync();
    }

    public void setTarget(UUID target) {
        this.target = target;
        this.hasTarget = true;
        this.sync();
    }

    public void abilityFail() {
        this.target = null;
        this.hasTarget = false;
        this.sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (this.target != null)  {tag.putUuid("target", this.target); }
        tag.putInt("glowTicks", this.glowTicks);
        tag.putBoolean("hasTarget", this.hasTarget);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("target")) {this.target = tag.contains("target") ? tag.getUuid("target") : null;}
        else {this.target = null;}
        this.glowTicks = tag.contains("glowTicks") ? tag.getInt("glowTicks") : 0;
        this.hasTarget = tag.contains("hasTarget") && tag.getBoolean("hasTarget");
    }
}