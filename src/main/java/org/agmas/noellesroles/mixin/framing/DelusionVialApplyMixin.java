package org.agmas.noellesroles.mixin.framing;

import dev.doctor4t.wathe.block.FoodPlatterBlock;
import dev.doctor4t.wathe.block_entity.BeveragePlateBlockEntity;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.jester.JesterPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodPlatterBlock.class)
public abstract class DelusionVialApplyMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void defenseVialApply(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) return;
        if (player.isCreative()) return;
        BlockEntity platter = world.getBlockEntity(pos);
        if (platter instanceof BeveragePlateBlockEntity blockEntity) {
            if (player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.DELUSION_VIAL) && blockEntity.getPoisoner() == null) {
                blockEntity.setPoisoner(player.getUuidAsString());
                player.getStackInHand(Hand.MAIN_HAND).decrement(1);
                player.playSoundToPlayer(SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.5F, 1.0F);
                GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
                if (gameWorldComponent.isRole(player, Noellesroles.JESTER)) {
                    JesterPlayerComponent jesterPlayerComponent = JesterPlayerComponent.KEY.get(player);
                    jesterPlayerComponent.jestCount += jesterPlayerComponent.jestDelusionUsed;
                    jesterPlayerComponent.sync();
                }
                cir.setReturnValue(ActionResult.SUCCESS);
                cir.cancel();
            }
        }
    }
}
