package dev.dokko.tellurium.mixin.entity;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugScreenEntryList.class)
public class DebugHudProfileMixin {

    @Inject(
            method = "isCurrentlyEnabled",
            at = @At("HEAD"),
            cancellable = true
    )
    private void forceHitboxes(
            Identifier entryId, CallbackInfoReturnable<Boolean> cir
    ) {
        if (entryId == DebugScreenEntries.ENTITY_HITBOXES) {
            cir.setReturnValue(true);
        }
    }
}