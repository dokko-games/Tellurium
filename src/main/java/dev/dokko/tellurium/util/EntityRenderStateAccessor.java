package dev.dokko.tellurium.util;

import net.minecraft.world.entity.Entity;

public interface EntityRenderStateAccessor {

    void setEntityOverride_combat_hitboxes(Entity ent);

    Entity getEntityOverride_combat_hitboxes();

}