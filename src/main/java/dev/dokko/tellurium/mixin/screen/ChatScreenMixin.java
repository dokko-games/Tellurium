package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Flags;
import dev.dokko.tellurium.Tellurium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow
    protected EditBox input;

    @Redirect(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Options;getBackgroundColor(I)I"
            )
    )
    private int hideChatBarBackground(Options instance, int defaultColor) {
        return Tellurium.getManager().getConfig().getChatTextBoxOpacity() << 24;
    }

    // On chat open
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        //
        Flags.STORE_CHAT_RESET = false;
        Flags.GUI_SNEAK_FLAG_TYPED = false;
        if(!Tellurium.getManager().getConfig().isStoreChatMessage())return;
        if(Flags.STORE_CHAT_LAST_MESSAGE == null)return;
        input.setValue(Flags.STORE_CHAT_LAST_MESSAGE);
    }
    @Inject(method = "handleChatInput", at = @At("HEAD"))
    public void injectSend(String msg, boolean addToRecent, CallbackInfo ci) {
        Flags.STORE_CHAT_LAST_MESSAGE = null;
        Flags.STORE_CHAT_RESET = true;

    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onRemoved(CallbackInfo ci) {
        // Save the current input
        if(!Flags.STORE_CHAT_RESET) Flags.STORE_CHAT_LAST_MESSAGE = input.getValue();
    }
    @Inject(method = "keyPressed", at=@At("HEAD"))
    private void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir){
        if(event.key() == GLFW.GLFW_KEY_BACKSPACE && !Flags.GUI_SNEAK_FLAG_TYPED && !Flags.STORE_CHAT_LAST_MESSAGE.isEmpty()){
            this.input.setValue("");
        }
        Flags.GUI_SNEAK_FLAG_TYPED = true;
    }
    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String msg, boolean addToRecent, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if(client.player == null)return;
        msg = ((ChatScreen)(Object)this).normalizeChatMessage(msg);

        if (!msg.isEmpty()) {
            if (addToRecent) {
                client.gui.getChat().addRecentChat(msg);
            }

            if (msg.startsWith("/")) {
                client.player.connection.sendCommand(msg.substring(1));
            } else {
                client.player.connection.sendChat(msg);
            }

        }
        // Cancel original method
        ci.cancel();
    }
}