package dev.dokko.tellurium.config.screen.hitbox;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import static dev.dokko.tellurium.config.screen.ConfigScreen.*;


public class PassiveEntityHitboxColorScreen extends AbstractConfigScreen<Config> {
    protected PassiveEntityHitboxColorScreen(Screen parent) {
        super(Tellurium.MOD_ID+".hitboxes.color.passive", parent, Tellurium.getManager());
    }
    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[] {
                makeFloat("red", config.getPassiveR(), config::setPassiveR, 0, 1, (float) 1 / 255),
                makeFloat("green", config.getPassiveG(), config::setPassiveG, 0, 1, (float) 1 / 255),
                makeFloat("blue", config.getPassiveB(), config::setPassiveB, 0, 1, (float) 1 / 255),
                makeFloat("alpha", config.getPassiveA(), config::setPassiveA, 0, 1, (float) 1 / 255),
        };
    }
}
