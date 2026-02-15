package org.agmas.noellesroles.client.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.entities.ShortFuseFirecrackerEntity;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class ShortFuseFirecrackerEntityRenderer extends EntityRenderer<ShortFuseFirecrackerEntity> {
    private final ItemRenderer itemRenderer;
    private final float scale;

    public ShortFuseFirecrackerEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
    }

    public ShortFuseFirecrackerEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0F);
    }

    @Override
    public Identifier getTexture(ShortFuseFirecrackerEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    public void render(@NotNull ShortFuseFirecrackerEntity shortFuseFireCracker, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (shortFuseFireCracker.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(shortFuseFireCracker) < 12.25)) {
            matrices.push();
            matrices.scale(this.scale, this.scale, this.scale);
            matrices.translate(0, shortFuseFireCracker.hashCode() % 30 / 1000f, 0); // prevent z-fighting
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-shortFuseFireCracker.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            this.itemRenderer
                    .renderItem(
                            ModItems.SHORTFUSE_FIRECRACKER.getDefaultStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, shortFuseFireCracker.getWorld(), shortFuseFireCracker.getId()
                    );
            matrices.pop();
            super.render(shortFuseFireCracker, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }
}