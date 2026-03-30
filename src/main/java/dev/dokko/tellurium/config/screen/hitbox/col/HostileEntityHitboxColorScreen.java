package dev.dokko.tellurium.config.screen.hitbox.col;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import net.minecraft.client.gui.screens.Screen;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import static dev.dokko.tellurium.config.screen.ConfigScreen.makeFloat;


public class HostileEntityHitboxColorScreen extends AbstractConfigScreen<Config> {
    protected HostileEntityHitboxColorScreen(Screen parent) {
        super(Tellurium.MOD_ID+".hitboxes.color.hostile", parent, Tellurium.getManager());
    }
    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[] {
                makeFloat("red", config.getHostileR(), config::setHostileR, 0, 1, (float) 1 / 255),
                makeFloat("green", config.getHostileG(), config::setHostileG, 0, 1, (float) 1 / 255),
                makeFloat("blue", config.getHostileB(), config::setHostileB, 0, 1, (float) 1 / 255),
                makeFloat("alpha", config.getHostileA(), config::setHostileA, 0, 1, (float) 1 / 255),
        };
    }
}
