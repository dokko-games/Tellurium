package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Flags;
import dev.dokko.tellurium.Tellurium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
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
    protected TextFieldWidget chatField;

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundColor(I)I"
            )
    )
    private int hideChatBarBackground(GameOptions options, int fallback) {
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
        chatField.setText(Flags.STORE_CHAT_LAST_MESSAGE);
    }
    @Inject(method = "sendMessage", at = @At("HEAD"))
    public void injectSend(String chatText, boolean addToHistory, CallbackInfo ci) {
        Flags.STORE_CHAT_LAST_MESSAGE = null;
        Flags.STORE_CHAT_RESET = true;

    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onRemoved(CallbackInfo ci) {
        // Save the current input
        if(!Flags.STORE_CHAT_RESET) Flags.STORE_CHAT_LAST_MESSAGE = chatField.getText();
    }
    @Inject(method = "keyPressed", at=@At("HEAD"))
    private void keyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir){
        if(input.key() == GLFW.GLFW_KEY_BACKSPACE && !Flags.GUI_SNEAK_FLAG_TYPED && !Flags.STORE_CHAT_LAST_MESSAGE.isEmpty()){
            chatField.setText("");
        }
        Flags.GUI_SNEAK_FLAG_TYPED = true;
    }
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, boolean addToHistory, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player == null)return;
        if(message.startsWith("/ccodes ")){
            if(message.startsWith("/ccodes fake")){
                message = message.substring(12);
                message = ((ChatScreen)(Object)this).normalize(message);
                if (!message.isEmpty()) {
                    if (addToHistory) {
                        client.inGameHud.getChatHud().addToMessageHistory(message);
                    }

                    if (message.startsWith("/")) {
                        client.player.networkHandler.sendChatCommand(message.substring(1));
                    } else {
                        client.player.networkHandler.sendChatMessage(message);
                    }

                }
            } else {
                message = message.substring(8);
                // Create the local styled version (what you see)
                Text styled = processCustomColors(message);
                // Show it in your local chat
                if(addToHistory) client.inGameHud.getChatHud().addToMessageHistory("/ccodes "+message);
                if(addToHistory) client.inGameHud.getChatHud().addMessage(styled);

            }
        } else {
            message = ((ChatScreen)(Object)this).normalize(message);
            if (!message.isEmpty()) {
                if (addToHistory) {
                    client.inGameHud.getChatHud().addToMessageHistory(message);
                }

                if (message.startsWith("/")) {
                    client.player.networkHandler.sendChatCommand(message.substring(1));
                } else {
                    client.player.networkHandler.sendChatMessage(message);
                }

            }
        }
        // Cancel original method
        ci.cancel();
    }
    @Unique
    private Text processCustomColors(String input) {
        MutableText result = Text.literal("");
        Matcher matcher = Pattern.compile("(\\.?)(\\$\\(([^)]+)\\))").matcher(input);

        int lastEnd = 0;
        Style currentStyle = Style.EMPTY;

        while (matcher.find()) {
            result.append(Text.literal(input.substring(lastEnd, matcher.start())).setStyle(currentStyle));
            boolean escaped = matcher.group(1).equals(".");
            String colorKey = matcher.group(3);

            if (escaped) {
                result.append(Text.literal(matcher.group(2)).setStyle(currentStyle)); // just $(...)
            } else {
                currentStyle = currentStyle.withColor(resolveColor(colorKey));
            }

            lastEnd = matcher.end();
        }

        // Append the remaining text
        result.append(Text.literal(input.substring(lastEnd)).setStyle(currentStyle));
        return result;
    }
    @Unique
    private TextColor resolveColor(String input) {
        try {
            return TextColor.parse(input.startsWith("#") ? input : "#" + input).getOrThrow();
        } catch (Exception e) {
            try {
                Formatting formatting = Formatting.valueOf(input.toUpperCase());
                return TextColor.fromFormatting(formatting);
            } catch (Exception ex) {
                return TextColor.fromFormatting(Formatting.RESET);
            }
        }
    }
}