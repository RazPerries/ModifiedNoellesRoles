package org.agmas.noellesroles.swapper;

import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.Random;

public class SwapperPlayerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<SwapperPlayerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "swapper"), SwapperPlayerComponent.class);
    private final PlayerEntity player;
    public int swapTicks = -1;
    // Minimum time for teleport (in ticks)
    public int minSwapTime = 30;
    // Maximum time for teleport (in ticks)
    public int maxSwapTime = 50;

    // Ability cost
    public int swapCost = 50;

    public PlayerEntity player1 = null;
    public PlayerEntity player2 = null;

    Vec3d swapperPos = null;
    Vec3d swappedPos = null;

    public void reset() {
        this.swapTicks = -1;
        this.swapperPos = null;
        this.swappedPos = null;
        this.player1 = null;
        this.player2 = null;
        this.sync();
    }

    public SwapperPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void serverTick() {
        if (this.swapTicks > 0) {
            --this.swapTicks;
        }
        if (this.swapTicks == 0) {
            if (player1 != null && player2 != null && !player1.isSpectator() && !player2.isSpectator()) {
                if (this.swapperPos != null && this.swappedPos != null) {
                    player1.teleport(swappedPos.x, swappedPos.y, swappedPos.z, false);
                    player2.teleport(swapperPos.x, swapperPos.y, swapperPos.z, false);
                }
            }
            swapTicks = -1;
        }
        this.sync();
    }

    public void getPlayerLocations(PlayerEntity player1, PlayerEntity player2) {
        this.player1 = player1;
        this.player2 = player2;
        Vec3d swapperPos = player1.getPos();
        Vec3d swappedPos = player2.getPos();
        this.swapperPos = swapperPos;
        this.swappedPos = swappedPos;
        this.sync();
    }

    public void setSwapTime() {
        Random random = new Random();
        this.swapTicks = random.nextInt(minSwapTime,maxSwapTime);
        this.sync();
    }

    public PlayerEntity getPlayer1() {
        return this.player1;
    }

    public PlayerEntity getPlayer2() {
        return this.player2;
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("swapTicks", this.swapTicks);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.swapTicks = tag.getInt("swapTicks");
    }
}
