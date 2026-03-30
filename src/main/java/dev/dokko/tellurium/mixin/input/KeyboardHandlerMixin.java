package dev.dokko.tellurium.mixin.input;

import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "handleDebugKeys", at = @At("RETURN"))
    private void onF3(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (event.key() == GLFW.GLFW_KEY_B) {
            HitboxConfig.setRenderHitboxes(!HitboxConfig.isRenderHitboxes());
        }
    }
}