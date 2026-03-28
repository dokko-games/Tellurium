package dev.dokko.tellurium.mixin.entity.render;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import org.joml.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Unique
    private static final ThreadLocal<Entity> CURRENT_ENTITY = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<Boolean> RENDERED = new ThreadLocal<>();

    /**
     * Capture entity for later use (since EntityHitbox doesn't expose it)
     */
    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private <E extends Entity> void captureEntity(
            E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci
    ) {
        CURRENT_ENTITY.set(entity);
        RENDERED.set(false);
    }

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("RETURN"))
    private <E extends Entity> void clearEntity(
            E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci
    ) {
        CURRENT_ENTITY.remove();
    }

    /**
     * Remove dead entities rendering
     */
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void onShouldRenderCall(
            E entity, Frustum frustum, double x, double y, double z,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!Tellurium.getManager().getConfig().isRemoveDeathAnimation()) return;

        if (!entity.isAlive()) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Cancel hitbox entirely
     */
    @Inject(method = "renderHitboxes(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/entity/state/EntityHitboxAndView;Lnet/minecraft/client/render/VertexConsumer;F)V", at = @At("HEAD"), cancellable = true)
    private static void cancelHitbox(
            MatrixStack matrices, EntityHitboxAndView hitbox, VertexConsumer vertexConsumer, float standingEyeHeight, CallbackInfo ci
    ) {
        Entity entity = CURRENT_ENTITY.get();
        if (entity == null) return;

        if (!HitboxConfig.renderCustomHitbox(entity)) {
            ci.cancel();
        }
    }

    /**
     * Modify main hitbox (now uses raw doubles instead of Box)
     */
    @Redirect(
            method = "renderHitbox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexRendering;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDDDDFFFF)V"
            )
    )
    private static void modifyMainBox(
            MatrixStack matrices,
            VertexConsumer vertices,
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ,
            float r, float g, float b, float a
    ) {
        Entity entity = CURRENT_ENTITY.get();
        if(RENDERED.get()) {
            handleEyeLine(matrices, vertices, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
            return;
        }
        if (entity == null) {
            VertexRendering.drawBox(matrices, vertices, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
            return;
        }

        float[] color = HitboxConfig.getColor(entity);

        VertexRendering.drawBox(
                matrices,
                vertices,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                color[0],
                color[1],
                color[2],
                color[3]
        );
        RENDERED.set(true);
    }

    /**
     * Remove eye-height line
     */
    @Unique
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
     * Remove look vector (now inside drawHitboxes)
     */
    @Redirect(
            method = "renderHitboxes(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/entity/state/EntityHitboxAndView;Lnet/minecraft/client/render/VertexConsumer;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexRendering;drawVector(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lorg/joml/Vector3f;Lnet/minecraft/util/math/Vec3d;I)V"
            )
    )
    private static void handleLookVector(
            MatrixStack matrices,
            VertexConsumer vertexConsumers,
            Vector3f start,
            Vec3d direction,
            int color
    ) {
        if (!HitboxConfig.isDisableLookVector()) {
            VertexRendering.drawVector(matrices, vertexConsumers, start, direction, color);
        }
    }
    @Inject(
            method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("TAIL")
    )
    private <E extends Entity> void onRenderEntity(
            E entity,
            double x, double y, double z,
            float tickProgress,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (dispatcher.shouldRenderHitboxes()) return;

        matrices.push();
        renderCustomHitboxes(entity, matrices, vertexConsumers, x, y, z);
        matrices.pop();
    }
    @Unique
    private static void renderCustomHitboxes(
            Entity entity,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            double cx, double cy, double cz
    ) {
        if (drawSpeedHitbox(entity, matrices, vertexConsumers, cx, cy, cz)) return;

        drawCrawlHitbox(entity, matrices, vertexConsumers, cx, cy, cz);
        drawEndCrystalElytraHitbox(entity, matrices, vertexConsumers, cx, cy, cz);
    }
    @Unique
    private static boolean drawSpeedHitbox(
            Entity entity,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            double cx, double cy, double cz
    ) {
        if (Tellurium.getManager().getConfig().isSpeedHitbox() && entity instanceof LivingEntity) {
            var player = MinecraftClient.getInstance().player;

            if (player != null && player.getVelocity().length() >
                    Tellurium.getManager().getConfig().getSpeedHitboxThreshold()) {

                drawBox(entity, matrices, vertexConsumers, cx, cy, cz);
                return true;
            }
        }
        return false;
    }
    @Unique
    private static void drawCrawlHitbox(
            Entity entity,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            double cx, double cy, double cz
    ) {
        if (entity instanceof PlayerEntity player) {
            if (Tellurium.getManager().getConfig().isCrawlHitbox()) {
                if (player.isSwimming() || player.isGliding() || player.isCrawling()) {
                    drawBox(entity, matrices, vertexConsumers, cx, cy, cz);
                }
            }
        }
    }
    @Unique
    private static void drawEndCrystalElytraHitbox(
            Entity entity,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            double cx, double cy, double cz
    ) {
        if (entity instanceof EndCrystalEntity) {
            if (Tellurium.getManager().getConfig().isElytraCrystalHitbox()) {
                var player = MinecraftClient.getInstance().player;

                if (player != null && player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
                    drawBox(entity, matrices, vertexConsumers, cx, cy, cz);
                }
            }
        }
    }
    @Unique
    private static void drawBox(
            Entity entity,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            double cx, double cy, double cz
    ) {

        Box box = entity.getBoundingBox().offset(
                -entity.getX()+ cx,
                -entity.getY() + cy,
                -entity.getZ() + cz
        );

        float[] color = HitboxConfig.getColor(entity);

        VertexRendering.drawBox(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getLines()),
                box,
                color[0],
                color[1],
                color[2],
                color[3]
        );
    }
}