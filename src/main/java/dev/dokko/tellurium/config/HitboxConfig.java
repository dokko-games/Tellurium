package dev.dokko.tellurium.config;

import dev.dokko.tellurium.Tellurium;
import lombok.Getter;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;


public class HitboxConfig {
    // runtime toggle for custom hitboxes
    @Getter
    private static boolean renderHitboxes = false;

    // Returns true if the entity's hitbox is normally rendered. Negated for conditional hitboxes
    public static boolean renderCustomHitbox(Entity entity) {
        if (!renderHitboxes) return false;

        var config = Tellurium.getConfig();

        if (entity instanceof Player && config.isHideHitboxesForPlayers()) return false;
        if (entity instanceof AgeableMob && config.isHideHitboxesForPassiveMobs()) return false;
        if (entity instanceof Monster && config.isHideHitboxesForHostileMobs()) return false;
        if (entity instanceof ItemEntity && config.isHideHitboxesForItems()) return false;
        return entity instanceof AgeableMob ||
                entity instanceof Monster ||
                entity instanceof Player ||
                entity instanceof ItemEntity ||
                !config.isHideHitboxesForNeutralMobs();
    }

    public static void setRenderHitboxes(boolean render) {
        renderHitboxes = render;
        Tellurium.getConfig().setRenderHitboxes(render);
        Tellurium.saveConfig();
    }

    public static float[] getColor(Entity entity) {
        var config = Tellurium.getConfig();
        if (entity instanceof Player) return new float[]{config.getPlayerR(), config.getPlayerG(), config.getPlayerB(), config.getPlayerA()};
        if (entity instanceof Monster) return new float[]{config.getHostileR(), config.getHostileG(), config.getHostileB(), config.getHostileA()};
        if (entity instanceof AgeableMob) return new float[]{config.getPassiveR(), config.getPassiveG(), config.getPassiveB(), config.getPassiveA()};
        if (entity instanceof ItemEntity) return new float[]{config.getItemR(), config.getItemG(), config.getItemB(), config.getItemA()};
        return new float[]{config.getNeutralR(), config.getNeutralG(), config.getNeutralB(), config.getNeutralA()};
    }

}