package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.util.Utils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Slf4j
@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    @Unique
    private static final Identifier DEFAULT_ICON = Identifier.fromNamespaceAndPath("tellurium", "/textures/icon/screenshots.png");

    @Unique
    private Button ssButton = null;

    @Unique
    private Button rpButton = null;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/options/OptionsScreen;repositionElements()V"))
    public void addSSButton(CallbackInfo ci) {

        this.rpButton = this.children().stream()
                .filter(c -> c instanceof Button b && b.getMessage().equals(Component.translatable("options.resourcepack")))
                .map(Button.class::cast)
                .findFirst()
                .orElse(null);

        if (this.rpButton == null) {
            log.error("Could not find the resource packs button to align the screenshots button!");
        }

        this.ssButton = this.addRenderableWidget(
                new Button.Builder(Component.empty(), ignored -> Util.getPlatform().openFile(Utils.getScreenshotsPath(minecraft)))
                        .size(20, 20)
                        .tooltip(Tooltip.create(Component.literal("Screenshots")))
                        .build()
        );
    }

    @Inject(method = "repositionElements", at = @At("RETURN"))
    public void refreshWidgetPositions(CallbackInfo ci) {
        if (this.ssButton != null && this.rpButton != null) {
            this.ssButton.setPosition(this.rpButton.getX() - 24, this.rpButton.getY());
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (this.ssButton != null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, DEFAULT_ICON, this.ssButton.getX() + 2, this.ssButton.getY() + 2, 0, 0, 16, 16, 16, 16);
        }
    }

    protected OptionsScreenMixin(Component title) {
        super(title);
    }
}