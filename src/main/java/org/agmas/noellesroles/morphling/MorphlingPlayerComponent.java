package org.agmas.noellesroles.morphling;

import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
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
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public class MorphlingPlayerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<MorphlingPlayerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "morphling"), MorphlingPlayerComponent.class);
    private final PlayerEntity player;
    public UUID disguise;
    public int morphTicks = 0;

    // Ability cost
    public int morphCost = 25;

    public void reset() {
        this.stopMorph();
        this.sync();
    }

    public MorphlingPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void serverTick() {
        if (this.morphTicks > 0 && this.disguise != null) {
            PlayerPsychoComponent playerPsychoComponent = PlayerPsychoComponent.KEY.get(player);
            if (playerPsychoComponent.psychoTicks > 0) {
                stopMorph();
            }

            if (--this.morphTicks == 0) {
                this.stopMorph();
            }

            this.sync();
        }

        if (this.morphTicks < 0) {
            this.morphTicks++;
            this.sync();
        }
    }

    public void startMorph(UUID id) {
        this.setMorphTicks(GameConstants.getInTicks(0,40));
        this.disguise = id;
        this.sync();
    }

    public void stopMorph() {
        this.morphTicks = -GameConstants.getInTicks(0,25);
    }

    public int getMorphTicks() {
        return this.morphTicks;
    }

    public void setMorphTicks(int ticks) {
        this.morphTicks = ticks;
        this.sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("morphTicks", this.morphTicks);
        if (disguise == null) disguise = player.getUuid();
        tag.putUuid("disguise", this.disguise);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.morphTicks = tag.contains("morphTicks") ? tag.getInt("morphTicks") : 0;
        this.disguise = tag.contains("disguise") ? tag.getUuid("disguise") : player.getUuid();
    }
}
