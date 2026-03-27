package dev.dokko.tellurium.mixin.entity;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        perfectReachLogic(target);
    }

    @Unique
    private void perfectReachLogic(Entity target) {
        if(!Tellurium.getManager().getConfig().isPerfectReachSound()) return;
        PlayerEntity player = (PlayerEntity)(Object)this;
        World world = player.getWorld();

        // +.02 for calculation errors
        float error = .12f;

        if (target == null) return;

        Vec3d eyePos = player.getEyePos();

        Box box = target.getBoundingBox();

        double closestX = Math.max(box.minX, Math.min(eyePos.x, box.maxX));
        double closestY = Math.max(box.minY, Math.min(eyePos.y, box.maxY));
        double closestZ = Math.max(box.minZ, Math.min(eyePos.z, box.maxZ));

        Vec3d closestPoint = new Vec3d(closestX, closestY, closestZ);
        double distance = eyePos.distanceTo(closestPoint);

        // Check ~3 blocks with tolerance
        if (distance >= 3 - error && distance <= 3 + error) {
            world.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.UI_BUTTON_CLICK,
                    SoundCategory.PLAYERS,
                    .85f,
                    1.2f
            );
        }
    }

}
