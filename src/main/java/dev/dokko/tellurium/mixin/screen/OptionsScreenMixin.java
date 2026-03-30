package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.uku3lig.ukulib.utils.IconButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    @Unique
    private static final Identifier STATS_ICON = Identifier.of(Tellurium.MOD_ID, "textures/icon/screenshots.png");

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo ci) {
        ButtonWidget rp = this.children().stream()
                .filter(c -> c instanceof ButtonWidget b && b.getMessage().equals(Text.translatable("options.resourcepack")))
                .map(ButtonWidget.class::cast)
                .findFirst()
                .orElseGet(() -> ButtonWidget.builder(Text.empty(), b -> {}).build()); // should never happen
        ButtonWidget button = new IconButton(rp.getX() - 2 - 20, rp.getY(), 20, 20,
                STATS_ICON, 16, 16,
                a -> openScreenshotsFolder());
        button.setTooltip(Tooltip.of(Text.translatable(Tellurium.MOD_ID+".gui.screenshots")));
        this.addDrawableChild(button);
    }

    @Unique
    private void openScreenshotsFolder() {
        Util.getOperatingSystem().open(Utils.getScreenshotsPath(client));
    }

}
