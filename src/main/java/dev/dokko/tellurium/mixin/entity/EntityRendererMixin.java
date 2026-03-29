package dev.dokko.tellurium.mixin.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.opengl.GlStateManager;
import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void injectShouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir){
        if(!entity.isAlive() && Tellurium.getManager().getConfig().isRemoveDeathAnimation())cir.setReturnValue(false);
    }

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void tellurium$captureEntity(
            T entity,
            S state,
            float tickDelta,
            CallbackInfo ci
    ) {
        HitboxConfig.STATE_TO_ENTITY.put(state, entity);
    }

    @Inject(method = "createHitbox", at = @At("HEAD"), cancellable = true)
    private void tellurium$hitbox(
            T entity,
            float tickDelta,
            boolean green,
            CallbackInfoReturnable<EntityHitboxAndView> cir
    ) {
        var config = Tellurium.getManager().getConfig();
        var builder = ImmutableList.<EntityHitbox>builder();
        Box box = entity.getBoundingBox();
        float[] color = HitboxConfig.getColor(entity);

        boolean shouldRenderNormal = HitboxConfig.renderCustomHitbox(entity);

        EntityHitbox mainHitbox = null;

        if (shouldRenderNormal) {
            mainHitbox = new EntityHitbox(
                    box.minX - entity.getX(),
                    box.minY - entity.getY(),
                    box.minZ - entity.getZ(),
                    box.maxX - entity.getX(),
                    box.maxY - entity.getY(),
                    box.maxZ - entity.getZ(),
                    color[0], color[1], color[2]
            );
        } else {
            if (config.isSpeedHitbox() && MinecraftClient.getInstance().player.getVelocity().length() > config.getSpeedHitboxThreshold()) {
                mainHitbox = new EntityHitbox(
                        box.minX - entity.getX(),
                        box.minY - entity.getY(),
                        box.minZ - entity.getZ(),
                        box.maxX - entity.getX(),
                        box.maxY - entity.getY(),
                        box.maxZ - entity.getZ(),
                        color[0], color[1], color[2]
                );
            } else if (config.isCrawlHitbox() && entity.isSneaking() || entity.isSwimming() || (entity.getEntity() != null && entity.getEntity().isGliding())) {
                mainHitbox = new EntityHitbox(
                        box.minX - entity.getX(),
                        box.minY - entity.getY(),
                        box.minZ - entity.getZ(),
                        box.maxX - entity.getX(),
                        box.minY - entity.getY(),
                        box.maxZ - entity.getZ(),
                        color[0], color[1], color[2]
                );
            } else if (config.isElytraCrystalHitbox() && entity instanceof EndCrystalEntity &&
                    MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
                mainHitbox = new EntityHitbox(
                        box.minX - entity.getX(),
                        box.minY - entity.getY(),
                        box.minZ - entity.getZ(),
                        box.maxX - entity.getX(),
                        box.maxY - entity.getY(),
                        box.maxZ - entity.getZ(),
                        color[0], color[1], color[2]
                );
            }
        }
        HitboxConfig.currentHitbox.put(entity, mainHitbox);

        Vec3d rot = HitboxConfig.isDisableLookVector()
                ? Vec3d.ZERO
                : entity.getRotationVec(tickDelta);


        cir.setReturnValue(new EntityHitboxAndView(rot.x, rot.y, rot.z, builder.build()));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void tellurium$filled(
            S state,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            CameraRenderState camera,
            CallbackInfo ci
    ) {
        if (!HitboxConfig.isRenderHitboxes()) return;

        Entity entity = HitboxConfig.STATE_TO_ENTITY.get(state);
        if (entity == null) return;

        EntityHitbox main = HitboxConfig.currentHitbox.get(entity);
        if (main == null) return;

        var client = MinecraftClient.getInstance();
        var immediate = MinecraftClient.getInstance()
                .getBufferBuilders()
                .getEntityVertexConsumers();
        GlStateManager._disableDepthTest();
        var buffer = immediate
                .getBuffer(RenderLayer.getDebugFilledBox());

        float fillAlpha = Tellurium.getManager().getConfig().getHitboxFillOpacity();
        VertexRendering.drawFilledBox(
                matrices,
                buffer,
                main.x0(), main.y0(), main.z0(),
                main.x1(), main.y1(), main.z1(),
                main.red(), main.green(), main.blue(),
                fillAlpha
        );

        immediate.draw();
        GlStateManager._enableDepthTest();
        buffer = client.getBufferBuilders()
                .getEntityVertexConsumers()
                .getBuffer(RenderLayer.getLines());

        VertexRendering.drawBox(
                matrices.peek(),
                buffer,
                main.x0(), main.y0(), main.z0(),
                main.x1(), main.y1(), main.z1(),
                main.red(), main.green(), main.blue(),
                HitboxConfig.getColor(entity)[3]
        );

    }
}