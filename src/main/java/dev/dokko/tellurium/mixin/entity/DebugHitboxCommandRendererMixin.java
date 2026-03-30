package dev.dokko.tellurium.mixin.entity;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import dev.dokko.tellurium.config.HitboxConfig;
import dev.dokko.tellurium.util.EntityRenderStateAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.command.DebugHitboxCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(DebugHitboxCommandRenderer.class)
public abstract class DebugHitboxCommandRendererMixin {
    @Unique private static Entity renderEntity;

    @ModifyVariable(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/command/DebugHitboxCommandRenderer;renderDebugHitbox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/entity/state/EntityHitboxAndView;Lnet/minecraft/client/render/VertexConsumer;F)V"
            ),
            index = 4)
    private OrderedRenderCommandQueueImpl.DebugHitboxCommand captureState(OrderedRenderCommandQueueImpl.DebugHitboxCommand value) {
        renderEntity = ((EntityRenderStateAccessor) value.renderState()).getEntityOverride_combat_hitboxes();
        return value;
    }

    @Inject(method = "renderDebugHitbox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/entity/state/EntityHitboxAndView;Lnet/minecraft/client/render/VertexConsumer;F)V",
            at = @At("HEAD"), cancellable = true)
    private static void hitBoxHook(MatrixStack matrices, EntityHitboxAndView hitbox, net.minecraft.client.render.VertexConsumer vertexConsumer, float tickDelta, CallbackInfo ci) {
        ci.cancel();
        var config = Tellurium.getManager().getConfig();
        boolean render = true;
        // F3+B is off
        if(!HitboxConfig.isRenderHitboxes()){
            render = shouldRender(config);
        }
        else if(!HitboxConfig.renderCustomHitbox(renderEntity)){
            render = shouldRender(config);
        }
        if(!render)return;
        MinecraftClient mc = MinecraftClient.getInstance();
        VertexConsumerProvider.Immediate imm = mc.getBufferBuilders().getEntityVertexConsumers();


        float[] color = HitboxConfig.getColor(renderEntity);

        renderBox(
                matrices,
                vertexConsumer,
                renderEntity,
                tickDelta,
                new Color(255, 0, 0, 255),
                new Color(0, 0, 255, 255),
                new Color(color[0], color[1], color[2], color[3]),
                !config.isDisableEyeLine(),
                !config.isDisableLookVector()
        );

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        imm.draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    @Unique
    private static boolean shouldRender(Config config) {
        boolean render = true;
        boolean currentCheck = config.isSpeedHitbox() && MinecraftClient.getInstance().player.getVelocity().length() > config.getSpeedHitboxThreshold();
        if(!currentCheck) {
            currentCheck = config.isCrawlHitbox() && (renderEntity.isSneaking() || renderEntity.isSwimming() ||
                    (renderEntity.getEntity() != null && renderEntity.getEntity().isGliding()));
            if(!currentCheck) {
                currentCheck = config.isElytraCrystalHitbox() && renderEntity instanceof EndCrystalEntity &&
                        MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
                render = currentCheck;
            }
        }
        return render;
    }

    @Unique
    private static void renderBox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, Color eyeHeight, Color lookDir, Color main, boolean renderEyeHeight, boolean renderLookDir) {
        if (entity == null) return;
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

        VertexRendering.drawBox(matrices.peek(), vertices, box, main.getRed() / 255f, main.getGreen() / 255f, main.getBlue() / 255f, main.getAlpha() / 255f);


        renderDragon(entity, matrices, tickDelta, vertices);


        if (entity instanceof LivingEntity && renderEyeHeight) {
            float j = 0.01f;
            VertexRendering.drawBox(matrices.peek(), vertices, box.minX, entity.getStandingEyeHeight() - j, box.minZ, box.maxX, entity.getStandingEyeHeight() + j, box.maxZ, eyeHeight.getRed() / 255f, eyeHeight.getGreen() / 255f, eyeHeight.getBlue() / 255f, eyeHeight.getAlpha() / 255f);
        }
        Entity entity2;
        if ((entity2 = entity.getVehicle()) != null) {
            float k = Math.min(entity2.getWidth(), entity.getWidth()) / 2.0f;
            float l = 0.0625f;
            Vec3d vec3d = entity2.getPassengerRidingPos(entity).subtract(entity.getEntityPos());
            VertexRendering.drawBox(matrices.peek(), vertices, vec3d.x - (double)k, vec3d.y, vec3d.z - (double)k, vec3d.x + (double)k, vec3d.y + l, vec3d.z + (double)k, 1.0f, 1.0f, 0.0f, 1.0f);
        }
        if (renderLookDir) {
            Vec3d vec3d2 = entity.getRotationVec(tickDelta);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            MatrixStack.Entry matrix3f = matrices.peek();

            vertices.vertex(matrix4f, 0.0f, entity.getStandingEyeHeight(), 0.0f).color(lookDir.getRed(), lookDir.getGreen(), lookDir.getBlue(), lookDir.getAlpha()).normal(matrix3f, (float) vec3d2.x, (float) vec3d2.y, (float) vec3d2.z);
            vertices.vertex(matrix4f, (float) (vec3d2.x * 2.0), (float) ((double) entity.getStandingEyeHeight() + vec3d2.y * 2.0), (float) (vec3d2.z * 2.0)).color(lookDir.getRed(), lookDir.getGreen(), lookDir.getBlue(), lookDir.getAlpha()).normal(matrix3f, (float) vec3d2.x, (float) vec3d2.y, (float) vec3d2.z);
        }
    }

    @Unique
    private static void renderDragon(Entity entity, MatrixStack matrices, float tickDelta, VertexConsumer vertices) {
        if (entity instanceof EnderDragonEntity) {
            double d = -MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
            double e = -MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
            double f = -MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
            for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
                matrices.push();
                double g = d + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderX, enderDragonPart.getX());
                double h = e + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderY, enderDragonPart.getY());
                double i = f + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
                matrices.translate(g, h, i);
                VertexRendering.drawBox(matrices.peek(), vertices, enderDragonPart.getBoundingBox().offset(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()), 0.25f, 1.0f, 0.0f, 1.0f);
                matrices.pop();
            }
        }
    }
}