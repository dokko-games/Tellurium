package dev.dokko.tellurium.config;

import dev.dokko.tellurium.config.screen.ConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.uku3lig.ukulib.api.UkulibAPI;

import java.util.function.UnaryOperator;

public class Implementation implements UkulibAPI {
    @Override
    public UnaryOperator<Screen> supplyConfigScreen() {
        return ConfigScreen::new;
    }
}
