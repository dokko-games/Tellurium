package dev.dokko.tellurium.config.screen.hitbox;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import dev.dokko.tellurium.config.screen.hitbox.col.HitboxesColorScreen;
import dev.dokko.tellurium.config.screen.hitbox.cond.ConditionalHitboxesScreen;
import net.minecraft.client.gui.screens.Screen;
import net.uku3lig.ukulib.config.option.ScreenOpenButton;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import static dev.dokko.tellurium.config.screen.ConfigScreen.*;


public class HitboxesScreen extends AbstractConfigScreen<Config> {
    public HitboxesScreen(Screen parent) {
        super(Tellurium.MOD_ID+".hitboxes", parent, Tellurium.getManager());
    }
    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[] {
                makeBoolean("disableEyeLine", config.isDisableEyeLine(), config::setDisableEyeLine),
                makeBoolean("disableLookVector", config.isDisableLookVector(), config::setDisableLookVector),
                new ScreenOpenButton(Tellurium.MOD_ID+".hitboxes.conditional", ConditionalHitboxesScreen::new),
                new ScreenOpenButton(Tellurium.MOD_ID+".hitboxes.color", HitboxesColorScreen::new)
        };
    }
}
