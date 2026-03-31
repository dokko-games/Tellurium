package dev.dokko.tellurium.mixin.entity;

import dev.dokko.tellurium.Tellurium;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity entity, CallbackInfo ci) {
        perfectReachLogic(entity);
    }

    @Unique
    private void perfectReachLogic(Entity target) {
        if(!Tellurium.getConfig().isPerfectReachSound()) return;
        Player player = (Player)(Object)this;
        Level world = player.level();

        // +.02 for calculation errors
        float error = .12f;

        if (target == null) return;

        Vec3 eyePos = player.getEyePosition();

        AABB box = target.getBoundingBox();

        double closestX = Math.clamp(eyePos.x, box.minX, box.maxX);
        double closestY = Math.clamp(eyePos.y, box.minY, box.maxY);
        double closestZ = Math.clamp(eyePos.z, box.minZ, box.maxZ);

        Vec3 closestPoint = new Vec3(closestX, closestY, closestZ);
        double distance = eyePos.distanceTo(closestPoint);

        // Check ~3 blocks with tolerance
        if (distance >= 3 - error && distance <= 3 + error) {
            world.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.UI_BUTTON_CLICK,
                    SoundSource.PLAYERS,
                    .85f,
                    1.2f
            );
        }
    }

}
