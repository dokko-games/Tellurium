package dev.dokko.tellurium.mixin.entity;

import dev.dokko.tellurium.util.EntityRenderStateAccessor;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateAccessor {

    private Entity entityOverride_combat_hitboxes;

    public void setEntityOverride_combat_hitboxes(Entity ent) {
        entityOverride_combat_hitboxes = ent;
    }

    public Entity getEntityOverride_combat_hitboxes() {
        return entityOverride_combat_hitboxes;
    }
}