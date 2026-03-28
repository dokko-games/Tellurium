package dev.dokko.tellurium.mixin.entity.render;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void onShouldRenderCall(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir){
        if(!Tellurium.getManager().getConfig().isRemoveDeathAnimation())return;
        if(!entity.isAlive()){
            cir.setReturnValue(false);
        }
    }


    @Inject(
            method = "renderHitbox",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void cancelHitbox(
            MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo ci
    ) {
        if(!HitboxConfig.renderCustomHitbox(entity)){
            ci.cancel();
        }
    }

    /**
     * Modify MAIN hitbox
     */
    @Redirect(
            method = "renderHitbox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexRendering;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/math/Box;FFFF)V"
            )
    )
    private static void modifyMainBox(
            MatrixStack matrices,
            VertexConsumer vertices,
            Box box,
            float r,
            float g,
            float b,
            float a,
            MatrixStack m2,
            VertexConsumer v2,
            Entity entity,
            float tickDelta,
            float red,
            float green,
            float blue
    ) {
        float[] color = HitboxConfig.getColor(entity);

        // Outline
        VertexRendering.drawBox(
                matrices,
                vertices,
                box,
                color[0],
                color[1],
                color[2],
                color[3]
        );

    }

    /**
     * Remove eye-height line
     */
    @Redirect(
            method = "renderHitbox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexRendering;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDDDDFFFF)V"
            )
    )
    private static void handleEyeLine(
            MatrixStack matrices,
            VertexConsumer vertices,
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ,
            float r, float g, float b, float a
    ) {
        if (HitboxConfig.isDisableEyeLine()) return;

        VertexRendering.drawBox(matrices, vertices, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
    }

    /**
     * Remove look vector
     */
    @Redirect(
            method = "renderHitbox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexRendering;drawVector(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lorg/joml/Vector3f;Lnet/minecraft/util/math/Vec3d;I)V"
            )
    )
    private static void handleLookVector(
            MatrixStack matrices, VertexConsumer vertexConsumers, Vector3f start, Vec3d direction, int color
    ) {
        if (!HitboxConfig.isDisableLookVector()) {
            VertexRendering.drawVector(matrices, vertexConsumers, start, direction, color);
        }
    }
}
