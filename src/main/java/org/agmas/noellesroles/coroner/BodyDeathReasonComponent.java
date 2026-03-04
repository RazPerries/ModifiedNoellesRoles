package org.agmas.noellesroles.coroner;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class BodyDeathReasonComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<BodyDeathReasonComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "body_death_reason"), BodyDeathReasonComponent.class);
    public Identifier deathReason = GameConstants.DeathReasons.GENERIC;
    public Identifier playerRole = WatheRoles.CIVILIAN.identifier();
    public Identifier playerModifier = null;
    public boolean vultured = false;
    public PlayerBodyEntity playerBodyEntity;

    public void reset() {
        this.sync();
    }

    public BodyDeathReasonComponent(PlayerBodyEntity playerBodyEntity) {
        this.playerBodyEntity = playerBodyEntity;
    }

    public void sync() {
        KEY.sync(this.playerBodyEntity);
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("deathReason", deathReason.toString());
        tag.putString("playerRole", playerRole.toString());
        if (playerModifier != null) { tag.putString("playerModifier", playerModifier.toString()); }
        tag.putBoolean("vultured", vultured);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.deathReason = Identifier.of(tag.getString("deathReason"));
        this.playerRole = Identifier.of(tag.getString("playerRole"));
        if (tag.contains("playerModifier")) { this.playerModifier = Identifier.of(tag.getString("playerModifier")); }
        else { this.playerModifier = null; }
        this.vultured = tag.getBoolean("vultured");
    }

    @Override
    public void serverTick() {
        this.sync();
    }
}
