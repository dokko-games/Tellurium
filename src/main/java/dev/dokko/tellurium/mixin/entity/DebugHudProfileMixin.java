package dev.dokko.tellurium.mixin.entity;

import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudProfile;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugHudProfile.class)
public class DebugHudProfileMixin {

    @Inject(
            method = "isEntryVisible",
            at = @At("HEAD"),
            cancellable = true
    )
    private void forceHitboxes(
            Identifier entryId, CallbackInfoReturnable<Boolean> cir
    ) {
        if (entryId == DebugHudEntries.ENTITY_HITBOXES) {
            cir.setReturnValue(true);
        }
    }
}