package dev.dokko.tellurium.mixin.entity.render;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
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

@Mixin(EntityRenderer.class)
public class EntityRenderMixin<T extends Entity> {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderEntity(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        renderHitboxes(entity, matrices, vertexConsumers);
    }
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void onShouldRenderCall(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir){
        if(!Tellurium.getManager().getConfig().isRemoveDeathAnimation())return;
        if(!entity.isAlive()){
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static <T extends Entity> void renderHitboxes(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (drawSpeedHitbox(entity, matrices, vertexConsumers)) return;
        drawCrawlHitbox(entity, matrices, vertexConsumers);
        drawEndCrystalElytraHitbox(entity, matrices, vertexConsumers);
    }

    @Unique
    private static <T extends Entity> void drawEndCrystalElytraHitbox(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if(entity instanceof EndCrystalEntity){
            if(Tellurium.getManager().getConfig().isElytraCrystalHitbox()){
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player.getInventory().getArmorStack(2).isOf(Items.ELYTRA)) {
                    drawBox(entity, matrices, vertexConsumers);
                }
            }
        }
    }

    @Unique
    private static <T extends Entity> void drawCrawlHitbox(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (entity instanceof PlayerEntity player) {
            if(Tellurium.getManager().getConfig().isCrawlHitbox()){
                if (player.isSwimming() || player.isFallFlying() || player.isCrawling()) {
                    drawBox(entity, matrices, vertexConsumers);
                }
            }

        }
    }

    @Unique
    private static <T extends Entity> boolean drawSpeedHitbox(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if(Tellurium.getManager().getConfig().isSpeedHitbox() && entity instanceof LivingEntity){
            if(MinecraftClient.getInstance().player.getVelocity().length() > Tellurium.getManager().getConfig().getSpeedHitboxThreshold()){
                drawBox(entity, matrices, vertexConsumers);
                return true;
            }
        }
        return false;
    }

    private static <T extends Entity> void drawBox(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if(HitboxConfig.renderCustomHitbox(entity))return;
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertexConsumers.getBuffer(RenderLayer.LINES), box, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
