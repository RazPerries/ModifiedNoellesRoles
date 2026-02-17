package org.agmas.noellesroles.phantom;

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

public class PhantomPlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public static final ComponentKey<PhantomPlayerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "phantom"), PhantomPlayerComponent.class);
    private final PlayerEntity player;
    // How many invis charges phantom has
    public int invisCount = 3;
    // How long invis lasts for
    public int invisTimer = 20;
    // Cooldown goes down during invisibility. Add timer to cooldown to compensate.
    public int invisCooldown = 80;

    public void reset() {
        this.invisCount = 3;
        this.sync();
    }

    public PhantomPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void clientTick() {
    }

    public void serverTick() {
        this.sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("invisCount", this.invisCount);

    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.invisCount = tag.getInt("invisCount");
    }
}
