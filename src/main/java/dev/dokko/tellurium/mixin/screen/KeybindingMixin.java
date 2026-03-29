package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.Flags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class KeybindingMixin {

    @Shadow public abstract String getBoundKeyTranslationKey();

    @Inject(at = @At("HEAD"), method = "isPressed", cancellable = true)
    private void injectIsPressed(CallbackInfoReturnable<Boolean> ci){
        if(!Tellurium.getManager().getConfig().isGuiSneak())return;
        if(MinecraftClient.getInstance().currentScreen == null)return;
        if(getBoundKeyTranslationKey().equals(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey())){
            if(Flags.GUI_SNEAK_FLAG_SNEAKING){
                ci.setReturnValue(true);
            }
        }
    }
}
