package org.agmas.noellesroles.item;

import dev.doctor4t.wathe.entity.FirecrackerEntity;
import dev.doctor4t.wathe.index.WatheEntities;
import dev.doctor4t.wathe.util.AdventureUsable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.agmas.noellesroles.NoellesRolesEntities;
import org.agmas.noellesroles.entities.ShortFuseFirecrackerEntity;
import org.jetbrains.annotations.NotNull;

public class ShortFuseFirecrackerItem extends Item implements AdventureUsable {
    public ShortFuseFirecrackerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        if (context.getSide().equals(Direction.UP)) {
            PlayerEntity player = context.getPlayer();
            World world = player.getWorld();
            if (!world.isClient) {
                ShortFuseFirecrackerEntity shortfusefirecracker = NoellesRolesEntities.SHORTFUSE_FIRECRACKER_ENTITY_ENTITY_TYPE.create(world);
                Vec3d spawnPos = context.getHitPos();

                shortfusefirecracker.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                shortfusefirecracker.setYaw(player.getHeadYaw());
                world.spawnEntity(shortfusefirecracker);
                if (!player.isCreative()) player.getStackInHand(context.getHand()).decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}