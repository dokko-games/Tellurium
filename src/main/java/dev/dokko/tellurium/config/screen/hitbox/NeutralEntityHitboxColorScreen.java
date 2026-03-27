package dev.dokko.tellurium.config.screen.hitbox;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import static dev.dokko.tellurium.config.screen.ConfigScreen.makeFloat;


public class NeutralEntityHitboxColorScreen extends AbstractConfigScreen<Config> {
    protected NeutralEntityHitboxColorScreen(Screen parent) {
        super(Tellurium.MOD_ID+".hitboxes.color.neutral", parent, Tellurium.getManager());
    }
    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[] {
                makeFloat("red", config.getNeutralR(), config::setNeutralR, 0, 1, (float) 1 / 255),
                makeFloat("green", config.getNeutralG(), config::setNeutralG, 0, 1, (float) 1 / 255),
                makeFloat("blue", config.getNeutralB(), config::setNeutralB, 0, 1, (float) 1 / 255),
                makeFloat("alpha", config.getNeutralA(), config::setNeutralA, 0, 1, (float) 1 / 255),
        };
    }
}
