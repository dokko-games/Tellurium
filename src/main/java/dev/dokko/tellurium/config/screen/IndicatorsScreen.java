package dev.dokko.tellurium.config.screen;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import static dev.dokko.tellurium.config.screen.ConfigScreen.*;

public class IndicatorsScreen extends AbstractConfigScreen<Config> {
    protected IndicatorsScreen(Screen parent) {
        super(Tellurium.MOD_ID+".indicators", parent, Tellurium.getManager());
    }
    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[] {
                makeInt("indicators.offset", config.getIndicatorOffset(), config::setIndicatorOffset, -25, 25, 1),
                makeInt("indicators.size", config.getIndicatorSize(), config::setIndicatorSize, 2, 20, 1),
                makeBoolean("shieldStunIndicator", config.isShieldStunIndicator(), config::setShieldStunIndicator),
                makeBoolean("maceSlowFallIndicator", config.isMaceSlowFallIndicator(), config::setMaceSlowFallIndicator),
                makeBoolean("elytraIndicator", config.isElytraIndicator(), config::setElytraIndicator),
                makeBoolean("lowHealthIndicator", config.isLowHealthIndicator(), config::setLowHealthIndicator),
                makeBoolean("burningIndicator", config.isBurningIndicator(), config::setBurningIndicator),
                makeBoolean("repotIndicator", config.isRepotIndicator(), config::setRepotIndicator),
                makeBoolean("lowDurabilityIndicator", config.isLowDurabilityIndicator(), config::setLowDurabilityIndicator),
                makeBoolean("totemIndicator", config.isTotemIndicator(), config::setTotemIndicator),
        };
    }
}
