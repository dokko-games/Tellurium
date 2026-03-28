package dev.dokko.tellurium.mixin.entity.render;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class EntityRenderMixin {

    @Inject(
            method = "renderEntity",
            at = @At("TAIL")
    )
    private void onRenderEntity(
            Entity entity,
            double cx, double cy, double cz,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            CallbackInfo ci
    ) {
        matrices.push();

        renderHitboxes(entity, matrices, vertexConsumers, cx, cy, cz);

        matrices.pop();
    }

    private static void renderHitboxes(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cx, double cy, double cz) {
        if (drawSpeedHitbox(entity, matrices, vertexConsumers, cx, cy, cz)) return;
        drawCrawlHitbox(entity, matrices, vertexConsumers, cx, cy, cz);
        drawEndCrystalElytraHitbox(entity, matrices, vertexConsumers, cx, cy, cz);
    }

    private static void drawEndCrystalElytraHitbox(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cx, double cy, double cz) {
        if (entity instanceof EndCrystalEntity) {
            if (Tellurium.getManager().getConfig().isElytraCrystalHitbox()) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null && player.getInventory().getArmorStack(2).isOf(Items.ELYTRA)) {
                    drawBox(entity, matrices, vertexConsumers, cx, cy, cz);
                }
            }
        }
    }

    private static void drawCrawlHitbox(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cx, double cy, double cz) {
        if (entity instanceof PlayerEntity player) {
            if (Tellurium.getManager().getConfig().isCrawlHitbox()) {
                if (player.isSwimming() || player.isGliding() || player.isCrawling()) {
                    drawBox(entity, matrices, vertexConsumers, cx, cy, cz);
                }
            }
        }
    }

    private static boolean drawSpeedHitbox(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cx, double cy, double cz) {
        if (Tellurium.getManager().getConfig().isSpeedHitbox() && entity instanceof LivingEntity) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.getVelocity().length() >
                    Tellurium.getManager().getConfig().getSpeedHitboxThreshold()) {

                drawBox(entity, matrices, vertexConsumers, cx, cy, cz);
                return true;
            }
        }
        return false;
    }

    private static void drawBox(Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cx, double cy, double cz) {
        if (HitboxConfig.renderCustomHitbox(entity)) return;

        Box box = entity.getBoundingBox().offset(
                -cx,
                -cy,
                -cz
        );

        VertexRendering.drawBox(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.LINES),
                box,
                1.0F, 1.0F, 1.0F, 1.0F
        );
    }
}