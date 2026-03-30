package dev.dokko.tellurium.config.screen.hitbox.cond;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import net.minecraft.client.gui.screens.Screen;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import static dev.dokko.tellurium.config.screen.ConfigScreen.*;


public class ConditionalHitboxesScreen extends AbstractConfigScreen<Config> {
    public ConditionalHitboxesScreen(Screen parent) {
        super(Tellurium.MOD_ID+".hitboxes.conditional", parent, Tellurium.getManager());
    }
    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[] {
                makeNTBoolean("hideForPassive", config.isHideHitboxesForPassiveMobs(), config::setHideHitboxesForPassiveMobs),
                makeNTBoolean("hideForNeutral", config.isHideHitboxesForNeutralMobs(), config::setHideHitboxesForNeutralMobs),
                makeNTBoolean("hideForHostile", config.isHideHitboxesForHostileMobs(), config::setHideHitboxesForHostileMobs),
                makeNTBoolean("hideForPlayers", config.isHideHitboxesForPlayers(), config::setHideHitboxesForPlayers),
                makeBoolean("crawlHitbox", config.isCrawlHitbox(), config::setCrawlHitbox),
                makeBoolean("elytraCrystalHitbox", config.isElytraCrystalHitbox(), config::setElytraCrystalHitbox),
                makeBoolean("speedHitbox", config.isSpeedHitbox(), config::setSpeedHitbox),
                makeDouble("speedHitboxThreshold", config.getSpeedHitboxThreshold(), config::setSpeedHitboxThreshold, 0.6f, 5f, .1f),
        };
    }
}
