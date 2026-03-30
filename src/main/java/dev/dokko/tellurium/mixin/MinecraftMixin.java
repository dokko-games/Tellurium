package dev.dokko.tellurium.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.Flags;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Final public Options options;

    @Shadow public abstract Window getWindow();

    @Inject(method ="setScreen", at = @At("HEAD"))
    private void openScreen(Screen screen, CallbackInfo ci){
        if (Tellurium.getManager().getConfig().isGuiSneak() && screen != null) {
            Flags.GUI_SNEAK_FLAG_SNEAKING = InputConstants.isKeyDown(getWindow(), options.keyShift.getDefaultKey().getValue());
        }
    }
    @Inject(method = "run", at = @At("HEAD"))
    private void applyHitboxState(CallbackInfo ci) {
        HitboxConfig.setRenderHitboxes(Tellurium.getManager().getConfig().isRenderHitboxes());
        Minecraft.getInstance()
                .debugEntries
                .setStatus(DebugScreenEntries.ENTITY_HITBOXES, HitboxConfig.isRenderHitboxes() ? DebugScreenEntryStatus.ALWAYS_ON : DebugScreenEntryStatus.NEVER);
        Tellurium.LOGGER.info("Hitbox state applied: {}", HitboxConfig.isRenderHitboxes());
    }
}
