package dev.dokko.tellurium.config.screen.hitbox;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.uku3lig.ukulib.config.option.ScreenOpenButton;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import static dev.dokko.tellurium.config.screen.ConfigScreen.makeFloat;


public class HitboxesColorScreen extends AbstractConfigScreen<Config> {
    protected HitboxesColorScreen(Screen parent) {
        super(Tellurium.MOD_ID+".hitboxes.color", parent, Tellurium.getManager());
    }
    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[] {
                makeFloat("dwadwadwa", config.getHitboxFillOpacity(), config::setHitboxFillOpacity, 0, 1, 0.1f),
                new ScreenOpenButton(Tellurium.MOD_ID+".hitboxes.color.passive", PassiveEntityHitboxColorScreen::new),
                new ScreenOpenButton(Tellurium.MOD_ID+".hitboxes.color.neutral", NeutralEntityHitboxColorScreen::new),
                new ScreenOpenButton(Tellurium.MOD_ID+".hitboxes.color.hostile", HostileEntityHitboxColorScreen::new),
                new ScreenOpenButton(Tellurium.MOD_ID+".hitboxes.color.player", PlayerHitboxColorScreen::new),
        };
    }
}
