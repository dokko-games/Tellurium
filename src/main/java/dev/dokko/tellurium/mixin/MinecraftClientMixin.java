package dev.dokko.tellurium.mixin;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.Flags;
import dev.dokko.tellurium.config.HitboxConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Final public GameOptions options;

    @Shadow public abstract Window getWindow();

    @Inject(method ="setScreen", at = @At("HEAD"))
    private void openScreen(Screen screen, CallbackInfo ci){
        if (Tellurium.getManager().getConfig().isGuiSneak() && screen != null) {
            Flags.GUI_SNEAK_FLAG_SNEAKING = InputUtil.isKeyPressed(getWindow().getHandle(), options.sneakKey.getDefaultKey().getCode());
        }
    }
    @Inject(method = "run", at = @At("HEAD"))
    private void applyHitboxState(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient)(Object)this;
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();

        dispatcher.setRenderHitboxes(HitboxConfig.isRenderHitboxes());
        Tellurium.LOGGER.info("Stored Hitbox State: {}", dispatcher.shouldRenderHitboxes());
    }
}
