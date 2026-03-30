package dev.dokko.tellurium.mixin.entity;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.util.EntityRenderStateAccessor;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
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

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void onUpdateRenderState(Entity entity, EntityRenderState state, float tickProgress, CallbackInfo ci) {
        ((EntityRenderStateAccessor) state).setEntityOverride_combat_hitboxes(entity);
    }
}