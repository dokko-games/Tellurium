package dev.dokko.tellurium.mixin.input;

import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "processF3", at = @At("RETURN"))
    private void onF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GLFW.GLFW_KEY_B) {
            EntityRenderDispatcher dispatcher =
                    MinecraftClient.getInstance().getEntityRenderDispatcher();

            HitboxConfig.setRenderHitboxes(dispatcher.shouldRenderHitboxes());
            System.out.println(dispatcher.shouldRenderHitboxes());
        }
    }
}