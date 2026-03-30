package dev.dokko.tellurium.mixin.entity;

import dev.dokko.tellurium.util.EntityRenderStateAccessor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Setter
@Getter
@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateAccessor {

    @Unique
    private Entity entityOverride_combat_hitboxes;

}