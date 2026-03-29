package dev.dokko.tellurium.mixin.input;

import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "processF3", at = @At("RETURN"))
    private void onF3(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (keyInput.key() == GLFW.GLFW_KEY_B) {
            HitboxConfig.setRenderHitboxes(!HitboxConfig.isRenderHitboxes());
        }
    }
}