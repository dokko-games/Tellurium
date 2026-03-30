package dev.dokko.tellurium.config;

import dev.dokko.tellurium.Tellurium;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;


public class HitboxConfig {
    // runtime toggle for custom hitboxes
    private static boolean renderHitboxes = false;

    // Returns true if the entity's hitbox is normally rendered. Negated for conditional hitboxes
    public static boolean renderCustomHitbox(Entity entity) {
        if (!renderHitboxes) return false;

        var config = Tellurium.getManager().getConfig();

        if (entity instanceof PassiveEntity && config.isHideHitboxesForPassiveMobs()) return false;
        if (entity instanceof HostileEntity && config.isHideHitboxesForHostileMobs()) return false;
        if (entity instanceof PlayerEntity && config.isHideHitboxesForPlayers()) return false;
        return entity instanceof PassiveEntity ||
                entity instanceof HostileEntity ||
                entity instanceof PlayerEntity ||
                !config.isHideHitboxesForNeutralMobs();
    }

    public static boolean isRenderHitboxes() {
        return renderHitboxes;
    }

    public static void setRenderHitboxes(boolean render) {
        renderHitboxes = render;
        Tellurium.getManager().getConfig().setRenderHitboxes(render);
        Tellurium.getManager().saveConfig();
    }

    public static float[] getColor(Entity entity) {
        var config = Tellurium.getManager().getConfig();
        if (entity instanceof PlayerEntity) return new float[]{config.getPlayerR(), config.getPlayerG(), config.getPlayerB(), config.getPlayerA()};
        if (entity instanceof HostileEntity) return new float[]{config.getHostileR(), config.getHostileG(), config.getHostileB(), config.getHostileA()};
        if (entity instanceof PassiveEntity) return new float[]{config.getPassiveR(), config.getPassiveG(), config.getPassiveB(), config.getPassiveA()};
        return new float[]{config.getNeutralR(), config.getNeutralG(), config.getNeutralB(), config.getNeutralA()};
    }

    public static boolean isDisableEyeLine() {
        return Tellurium.getManager().getConfig().isDisableEyeLine();
    }

    public static boolean isDisableLookVector() {
        return Tellurium.getManager().getConfig().isDisableLookVector();
    }

}