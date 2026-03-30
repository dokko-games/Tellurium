package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Flags;
import dev.dokko.tellurium.Tellurium;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow
    protected EditBox input;

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Options;getBackgroundColor(I)I"
            )
    )
    private int hideChatBarBackground(Options instance, int i) {
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
    public void injectSend(String chatComponent, boolean addToHistory, CallbackInfo ci) {
        Flags.STORE_CHAT_LAST_MESSAGE = null;
        Flags.STORE_CHAT_RESET = true;

    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onRemoved(CallbackInfo ci) {
        // Save the current input
        if(!Flags.STORE_CHAT_RESET) Flags.STORE_CHAT_LAST_MESSAGE = input.getValue();
    }
    @Inject(method = "keyPressed", at=@At("HEAD"))
    private void keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir){
        if(keyEvent.key() == GLFW.GLFW_KEY_BACKSPACE && !Flags.GUI_SNEAK_FLAG_TYPED && !Flags.STORE_CHAT_LAST_MESSAGE.isEmpty()){
            this.input.setValue("");
        }
        Flags.GUI_SNEAK_FLAG_TYPED = true;
    }
    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, boolean addToHistory, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if(client.player == null)return;
        message = ((ChatScreen)(Object)this).normalizeChatMessage(message);

        if (!message.isEmpty()) {
            if (addToHistory) {
                client.gui.getChat().addRecentChat(message);
            }

            if (message.startsWith("/")) {
                client.player.connection.sendCommand(message.substring(1));
            } else {
                client.player.connection.sendChat(message);
            }

        }
        // Cancel original method
        ci.cancel();
    }
    @Unique
    private Component processCustomColors(String input) {
        MutableComponent result = Component.literal("");
        Matcher matcher = Pattern.compile("(\\.?)(\\$\\(([^)]+)\\))").matcher(input);

        int lastEnd = 0;
        Style currentStyle = Style.EMPTY;

        while (matcher.find()) {
            result.append(Component.literal(input.substring(lastEnd, matcher.start())).setStyle(currentStyle));
            boolean escaped = matcher.group(1).equals(".");
            String colorKey = matcher.group(3);

            if (escaped) {
                result.append(Component.literal(matcher.group(2)).setStyle(currentStyle)); // just $(...)
            } else {
                currentStyle = currentStyle.withColor(resolveColor(colorKey));
            }

            lastEnd = matcher.end();
        }

        // Append the remaining component
        result.append(Component.literal(input.substring(lastEnd)).setStyle(currentStyle));
        return result;
    }
    @Unique
    private TextColor resolveColor(String input) {
        try {
            return TextColor.parseColor(input.startsWith("#") ? input : "#" + input).getOrThrow();
        } catch (Exception e) {
            try {
                ChatFormatting formatting = ChatFormatting.valueOf(input.toUpperCase());
                return TextColor.fromLegacyFormat(formatting);
            } catch (Exception ex) {
                return TextColor.fromLegacyFormat(ChatFormatting.RESET);
            }
        }
    }
}