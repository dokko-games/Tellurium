package dev.dokko.tellurium.mixin.entity;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(EntityHitboxDebugRenderer.class)
public abstract class EntityHitboxDebugRendererMixin {

    @Inject(method = "showHitboxes", at = @At("HEAD"), cancellable = true)
    private void onDrawHitbox(Entity entity, float partialTicks, boolean isServerEntity, CallbackInfo ci) {
        if (isServerEntity) {// they want to debug, let them
            return;
        }
        Config config = Tellurium.getConfig();
        ci.cancel();
        Minecraft mc = Minecraft.getInstance();

        float[] col = HitboxConfig.getColor(entity);
        if(config.isRemoveDeathAnimation() && !entity.isAlive()) {
            return;
        }
        boolean render = true;
        // F3+B is off
        if(!HitboxConfig.isRenderHitboxes()){
            render = shouldRender(config, entity);
        }
        else if(!HitboxConfig.renderCustomHitbox(entity)){
            render = shouldRender(config, entity);
        }
        if(!render)return;

        render_1_21_11_boxes(mc, config.getHitboxThickness(), entity, partialTicks,
                new Color(255, 0, 0, 255),
                new Color(0, 0, 255, 255),
                new Color(col[0], col[1], col[2], col[3]),
                !config.isDisableEyeLine(),
                !config.isDisableLookVector()
        );
    }
    @Unique
    private static void render_1_21_11_boxes(Minecraft mc, float lineWidth, Entity entity, float tickProgress,
                                            Color eyeHeight,
                                            Color lookDir,
                                            Color main,
                                             boolean renderEyeHeight,
                                             boolean renderLookDir) {
        Vec3 vec3d = entity.position();
        Vec3 vec3d2 = entity.getPosition(tickProgress);
        Vec3 vec3d3 = vec3d2.subtract(vec3d);
        int i = main.getRGB();
        Gizmos.cuboid(entity.getBoundingBox().move(vec3d3), GizmoStyle.stroke(i, lineWidth));
        Gizmos.point(vec3d2, i, 2.0F);
        Entity entity2 = entity.getVehicle();
        if (entity2 != null) {
            float f = Math.min(entity2.getBbWidth(), entity.getBbWidth()) / 2.0F;
            float g = 0.0625F;
            Vec3 vec3d4 = entity2.getPassengerRidingPosition(entity).add(vec3d3);
            Gizmos.cuboid(new AABB(vec3d4.x - f, vec3d4.y, vec3d4.z - f, vec3d4.x + f, vec3d4.y + g, vec3d4.z + f), GizmoStyle.stroke(-256, lineWidth));
        }

        if (entity instanceof LivingEntity && renderEyeHeight) {
            AABB box = entity.getBoundingBox().move(vec3d3);
            float g = 0.01F;
            Gizmos.cuboid(
                    new AABB(box.minX, box.minY + entity.getEyeHeight() - g, box.minZ, box.maxX, box.minY + entity.getEyeHeight() + g, box.maxZ),
                    GizmoStyle.stroke(eyeHeight.getRGB(), lineWidth)
            );
        }

        if (entity instanceof EnderDragon enderDragonEntity) {
            for (EnderDragonPart enderDragonPart : enderDragonEntity.getSubEntities()) {
                Vec3 vec3d5 = enderDragonPart.position();
                Vec3 vec3d6 = enderDragonPart.getPosition(tickProgress);
                Vec3 vec3d7 = vec3d6.subtract(vec3d5);
                Gizmos.cuboid(enderDragonPart.getBoundingBox().move(vec3d7), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.25F, 1.0F, 0.0F)));
            }
        }

        Vec3 vec3d8 = vec3d2.add(0.0, entity.getEyeHeight(), 0.0);
        Vec3 vec3d9 = entity.getViewVector(tickProgress);
        if (renderLookDir) {
            Gizmos.line(vec3d8, vec3d8.add(vec3d9.scale(2.0)), lookDir.getRGB(), lineWidth);
        }
    }
    
    @Unique
    private static boolean shouldRender(Config config, Entity renderEntity) {
        boolean render = true;
        boolean currentCheck = config.isSpeedHitbox() && Minecraft.getInstance().player.getDeltaMovement().length() > config.getSpeedHitboxThreshold();
        if(!currentCheck) {
            currentCheck = config.isCrawlHitbox() && (renderEntity.isCrouching() || renderEntity.isSwimming() ||
                    (renderEntity.asLivingEntity() != null && renderEntity.asLivingEntity().isFallFlying()));
            if(!currentCheck) {
                currentCheck = config.isElytraCrystalHitbox() && renderEntity instanceof EndCrystal &&
                        Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
                render = currentCheck;
            }
        }
        return render;
    }
}