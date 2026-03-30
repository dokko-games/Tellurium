package dev.dokko.tellurium.config.screen.hitbox.col;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import net.minecraft.client.gui.screens.Screen;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import static dev.dokko.tellurium.config.screen.ConfigScreen.makeFloat;


public class PlayerHitboxColorScreen extends AbstractConfigScreen<Config> {
    protected PlayerHitboxColorScreen(Screen parent) {
        super(Tellurium.MOD_ID+".hitboxes.color.player", parent, Tellurium.getManager());
    }
    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[] {
                makeFloat("red", config.getPlayerR(), config::setPlayerR, 0, 1, (float) 1 / 255),
                makeFloat("green", config.getPlayerG(), config::setPlayerG, 0, 1, (float) 1 / 255),
                makeFloat("blue", config.getPlayerB(), config::setPlayerB, 0, 1, (float) 1 / 255),
                makeFloat("alpha", config.getPlayerA(), config::setPlayerA, 0, 1, (float) 1 / 255),
        };
    }
}
