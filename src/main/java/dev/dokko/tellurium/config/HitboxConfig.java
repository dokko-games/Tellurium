package dev.dokko.tellurium.config;

import dev.dokko.tellurium.Tellurium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public class HitboxConfig {

    public static boolean renderCustomHitbox(Entity entity){
        EntityRenderDispatcher dispatcher =
                MinecraftClient.getInstance().getEntityRenderDispatcher();
        if(!dispatcher.shouldRenderHitboxes())return false;
        var config = Tellurium.getManager().getConfig();

        if (entity instanceof PassiveEntity && config.isHideHitboxesForPassiveMobs()) {
            return false;
        }

        if (entity instanceof HostileEntity && config.isHideHitboxesForHostileMobs()) {
            return false;
        }

        if (entity instanceof PlayerEntity && config.isHideHitboxesForPlayers()) {
            return false;
        }

        // neutral (everything else)
        if (!(entity instanceof PassiveEntity) &&
                !(entity instanceof HostileEntity) &&
                !(entity instanceof PlayerEntity) &&
                config.isHideHitboxesForNeutralMobs()) {
            return false;
        }
        return true;
    }

    public static boolean isRenderHitboxes() {
        return Tellurium.getManager().getConfig().isRenderHitboxes();
    }
    public static void setRenderHitboxes(boolean render){
        Tellurium.getManager().getConfig().setRenderHitboxes(render);
        Tellurium.getManager().saveConfig();

    }

    public static float[] getColor(Entity entity) {
        Config config = Tellurium.getManager().getConfig();
        if (entity instanceof PlayerEntity) return new float[] {config.getPlayerR(), config.getPlayerG(), config.getPlayerB(), config.getPlayerA()};
        if (entity instanceof HostileEntity) return new float[] {config.getHostileR(), config.getHostileG(), config.getHostileB(), config.getHostileA()};
        if (entity instanceof PassiveEntity) return new float[] {config.getPassiveR(), config.getPassiveG(), config.getPassiveB(), config.getPassiveA()};
        return new float[] {config.getNeutralR(), config.getNeutralG(), config.getNeutralB(), config.getNeutralA()};
    }

    public static boolean isDisableEyeLine() {
        return Tellurium.getManager().getConfig().isDisableEyeLine();
    }

    public static boolean isDisableLookVector() {
        return Tellurium.getManager().getConfig().isDisableLookVector();
    }
}
