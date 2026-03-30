package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.Flags;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {

    @Shadow public abstract Component getTranslatedKeyMessage();

    @Inject(at = @At("HEAD"), method = "isDown", cancellable = true)
    private void injectIsDown(CallbackInfoReturnable<Boolean> ci){
        if(!Tellurium.getManager().getConfig().isGuiSneak())return;
        if(Minecraft.getInstance().screen == null)return;
        if(getTranslatedKeyMessage().equals(Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage())){
            if(Flags.GUI_SNEAK_FLAG_SNEAKING){
                ci.setReturnValue(true);
            }
        }
    }
}
