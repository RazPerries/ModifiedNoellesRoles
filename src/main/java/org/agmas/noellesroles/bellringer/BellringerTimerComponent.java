package org.agmas.noellesroles.bellringer;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class BellringerTimerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<BellringerTimerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "bellringer"), BellringerTimerComponent.class);

    public final World world;
    public int elapsedTicks;

    public BellringerTimerComponent(World world) {
        this.world = world;
        this.elapsedTicks = 0;
    }

    public void sync() {
        KEY.sync(this.world);
    }

    @Override
    public void serverTick() {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.world);

        if (gameWorld.isRunning()) {
            this.elapsedTicks++;
            if (this.elapsedTicks % 20 == 0) {
                this.sync();
            }
        } else if (gameWorld.getGameStatus() == GameWorldComponent.GameStatus.INACTIVE && this.elapsedTicks != 0) {
            this.elapsedTicks = 0;
            this.sync();
        }
    }

    public int getElapsedTicks() {
        return this.elapsedTicks;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("elapsedTicks", this.elapsedTicks);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.elapsedTicks = tag.getInt("elapsedTicks");
    }
}