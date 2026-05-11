package org.agmas.noellesroles.framing;

import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConspiratorShopEntry extends ShopEntry {

    public ConspiratorShopEntry(ItemStack stack, int price, Type type) {
        super(stack, price, type);
    }

    @Override
    public boolean onBuy(@NotNull PlayerEntity player) {
        return insertStackInFreeSlot(player, stack().copy());
    }
}
