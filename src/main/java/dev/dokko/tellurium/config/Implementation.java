package dev.dokko.tellurium.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.dokko.tellurium.Tellurium;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.clothconfig2.gui.entries.FloatListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.Consumer;


public class Implementation implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            Config config = Tellurium.getConfig();
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("tellurium.config.screen"));
            builder.setSavingRunnable(() -> ConfigManager.save(config));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(Component.translatable("tellurium.config.screen"));
            addGeneralEntries(general, entryBuilder, config);
            ConfigCategory indicators = builder.getOrCreateCategory(Component.translatable("tellurium.indicators"));
            addIndicatorEntries(indicators, entryBuilder, config);
            ConfigCategory conditionalHitboxes = builder.getOrCreateCategory(Component.translatable("tellurium.hitboxes.conditional"));
            addConditionalEntries(conditionalHitboxes, entryBuilder, config);
            ConfigCategory visualHitboxes = builder.getOrCreateCategory(Component.translatable("tellurium.hitboxes.color"));
            addVisualEntries(visualHitboxes, entryBuilder, config);
            return builder.build();
        };
    }

    private static void addVisualEntries(ConfigCategory visualHitboxes, ConfigEntryBuilder entryBuilder, Config config) {
        visualHitboxes.addEntry(makeBool(entryBuilder, "hitbox.disable.eyeLine", config.isDisableEyeLine(), true, config::setDisableEyeLine));
        visualHitboxes.addEntry(makeBool(entryBuilder, "hitbox.disable.lookVector", config.isDisableLookVector(), true, config::setDisableLookVector));
        visualHitboxes.addEntry(makeFloat(entryBuilder, "hitbox.thickness", config.getHitboxThickness(), 3, 0, 10, config::setHitboxThickness));
        visualHitboxes.addEntry(makeColorf(entryBuilder, "hitbox.color.passive", config.getPassiveR(), config.getPassiveG(),
                config.getPassiveB(), .2f, 1, .2f, newVal -> {
            config.setPassiveR(newVal[0]);
            config.setPassiveG(newVal[1]);
            config.setPassiveB(newVal[2]);
                }));
        visualHitboxes.addEntry(makeFloatNT(entryBuilder, "hitbox.color.alpha", config.getPassiveA(), 1, 0, 1, config::setPassiveA));
        visualHitboxes.addEntry(makeColorf(entryBuilder, "hitbox.color.neutral", config.getNeutralR(), config.getNeutralG(),
                config.getNeutralB(), 1f, 1, .2f, newVal -> {
            config.setNeutralR(newVal[0]);
            config.setNeutralG(newVal[1]);
            config.setNeutralB(newVal[2]);
                }));
        visualHitboxes.addEntry(makeFloatNT(entryBuilder, "hitbox.color.alpha", config.getNeutralA(), 1, 0, 1, config::setNeutralA));
        visualHitboxes.addEntry(makeColorf(entryBuilder, "hitbox.color.hostile", config.getHostileR(), config.getHostileG(),
                config.getHostileB(), 1f, .2f, .2f, newVal -> {
                    config.setHostileR(newVal[0]);
                    config.setHostileG(newVal[1]);
                    config.setHostileB(newVal[2]);
                }));
        visualHitboxes.addEntry(makeFloatNT(entryBuilder, "hitbox.color.alpha", config.getHostileA(), 1, 0, 1, config::setHostileA));
        visualHitboxes.addEntry(makeColorf(entryBuilder, "hitbox.color.player", config.getPlayerR(), config.getPlayerG(),
                config.getPlayerB(), 1f, 1, .2f, newVal -> {
                    config.setPlayerR(newVal[0]);
                    config.setPlayerG(newVal[1]);
                    config.setPlayerB(newVal[2]);
                }));
        visualHitboxes.addEntry(makeFloatNT(entryBuilder, "hitbox.color.alpha", config.getPlayerA(), 1, 0, 1, config::setPlayerA));
        visualHitboxes.addEntry(makeColorf(entryBuilder, "hitbox.color.item", config.getItemR(), config.getItemG(),
                config.getItemB(), .2f, .2f, 1f, newVal -> {
                    config.setItemR(newVal[0]);
                    config.setItemG(newVal[1]);
                    config.setItemB(newVal[2]);
                }));
        visualHitboxes.addEntry(makeFloatNT(entryBuilder, "hitbox.color.alpha", config.getItemA(), 1, 0, 1, config::setItemA));
    }

    private static void addConditionalEntries(ConfigCategory conditionalHitboxes, ConfigEntryBuilder entryBuilder, Config config) {
        conditionalHitboxes.addEntry(makeNTBool(entryBuilder, "hideForPassive", config.isHideHitboxesForPassiveMobs(), true, config::setHideHitboxesForPassiveMobs));
        conditionalHitboxes.addEntry(makeNTBool(entryBuilder, "hideForNeutral", config.isHideHitboxesForNeutralMobs(), true, config::setHideHitboxesForNeutralMobs));
        conditionalHitboxes.addEntry(makeNTBool(entryBuilder, "hideForHostile", config.isHideHitboxesForHostileMobs(), false, config::setHideHitboxesForHostileMobs));
        conditionalHitboxes.addEntry(makeNTBool(entryBuilder, "hideForPlayers", config.isHideHitboxesForPlayers(), false, config::setHideHitboxesForPlayers));
        conditionalHitboxes.addEntry(makeNTBool(entryBuilder, "hideForItems", config.isHideHitboxesForItems(), true, config::setHideHitboxesForItems));
        conditionalHitboxes.addEntry(makeBool(entryBuilder, "crawlHitbox", config.isCrawlHitbox(), true, config::setCrawlHitbox));
        conditionalHitboxes.addEntry(makeBool(entryBuilder, "elytraCrystalHitbox", config.isElytraCrystalHitbox(), false, config::setElytraCrystalHitbox));
        conditionalHitboxes.addEntry(makeBool(entryBuilder, "speedHitbox", config.isSpeedHitbox(), true, config::setSpeedHitbox));
        conditionalHitboxes.addEntry(makeFloat(entryBuilder, "speedHitboxThreshold", config.getSpeedHitboxThreshold(), 1.4f, 0.6f, 5, config::setSpeedHitboxThreshold));
    }

    private static void addIndicatorEntries(ConfigCategory indicators, ConfigEntryBuilder entryBuilder, Config config) {
        indicators.addEntry(makeInt(entryBuilder, "indicators.offset", config.getIndicatorOffset(), 15, -25, 25, config::setIndicatorOffset));
        indicators.addEntry(makeInt(entryBuilder, "indicators.size", config.getIndicatorSize(), 10, 2, 20, config::setIndicatorSize));
        indicators.addEntry(makeBool(entryBuilder, "indicators.shieldStun", config.isShieldStunIndicator(), true, config::setShieldStunIndicator));
        indicators.addEntry(makeBool(entryBuilder, "indicators.maceSlowFall", config.isMaceSlowFallIndicator(), true, config::setMaceSlowFallIndicator));
        indicators.addEntry(makeBool(entryBuilder, "indicators.elytra", config.isElytraIndicator(), false, config::setElytraIndicator));
        indicators.addEntry(makeBool(entryBuilder, "indicators.lowHealth", config.isLowHealthIndicator(), true, config::setLowHealthIndicator));
        indicators.addEntry(makeBool(entryBuilder, "indicators.burning", config.isBurningIndicator(), true, config::setBurningIndicator));
        indicators.addEntry(makeBool(entryBuilder, "indicators.repot", config.isRepotIndicator(), false, config::setRepotIndicator));
        indicators.addEntry(makeBool(entryBuilder, "indicators.lowDurability", config.isLowDurabilityIndicator(), true, config::setLowDurabilityIndicator));
        indicators.addEntry(makeBool(entryBuilder, "indicators.totem", config.isTotemIndicator(), false, config::setTotemIndicator));
    }

    private static void addGeneralEntries(ConfigCategory general, ConfigEntryBuilder entryBuilder, Config config) {
        general.addEntry(makeBool(entryBuilder, "invertSprint", config.isInvertSprint(), false, config::setInvertSprint));
        general.addEntry(makeBool(entryBuilder, "guiSneak", config.isGuiSneak(), true, config::setGuiSneak));
        general.addEntry(makeBool(entryBuilder, "storeChatMessage", config.isStoreChatMessage(), true, config::setStoreChatMessage));
        general.addEntry(makeInt(entryBuilder, "chatTextBoxOpacity", config.getChatTextBoxOpacity(), 0x80, 0, 255, config::setChatTextBoxOpacity));
        general.addEntry(makeBool(entryBuilder, "removeDeathAnimations", config.isRemoveDeathAnimation(), false, config::setRemoveDeathAnimation));
        general.addEntry(makeBool(entryBuilder, "perfectReachSound", config.isPerfectReachSound(), false, config::setPerfectReachSound));
        general.addEntry(makeEnum(
                entryBuilder,
                "enchantmentDisplay",
                config.getEnchantmentDisplay(),
                EnchantmentDisplay.DEFAULT,
                config::setEnchantmentDisplay
        ));
    }

    private static BooleanListEntry makeBool(ConfigEntryBuilder entryBuilder, String key, boolean value, boolean defaultValue, Consumer<Boolean> setter) {
        return entryBuilder.startBooleanToggle(Component.translatable("tellurium.feature."+key), value)
                .setDefaultValue(defaultValue)
                .setTooltip(Component.translatable("tellurium.feature."+key+".tooltip"))
                .setSaveConsumer(setter)
                .build();
    }
    private static BooleanListEntry makeNTBool(ConfigEntryBuilder entryBuilder, String key, boolean value, boolean defaultValue, Consumer<Boolean> setter) {
        return entryBuilder.startBooleanToggle(Component.translatable("tellurium.feature."+key), value)
                .setDefaultValue(defaultValue)
                .setSaveConsumer(setter)
                .build();
    }
    private static IntegerSliderEntry makeInt(ConfigEntryBuilder entryBuilder, String key, int value, int defaultValue, int min, int max, Consumer<Integer> setter) {
        return entryBuilder.startIntSlider(Component.translatable("tellurium.feature."+key), value, min, max)
                .setDefaultValue(defaultValue)
                .setTooltip(Component.translatable("tellurium.feature."+key+".tooltip"))
                .setSaveConsumer(setter)
                .build();
    }
    private static FloatListEntry makeFloat(ConfigEntryBuilder entryBuilder, String key, float value, float defaultValue, float min, float max, Consumer<Float> setter) {
        return entryBuilder.startFloatField(Component.translatable("tellurium.feature."+key), value)
                .setDefaultValue(defaultValue)
                .setMin(min)
                .setMax(max)
                .setTooltip(Component.translatable("tellurium.feature."+key+".tooltip"))
                .setSaveConsumer(setter)
                .build();
    }
    private static FloatListEntry makeFloatNT(ConfigEntryBuilder entryBuilder, String key, float value, float defaultValue, float min, float max, Consumer<Float> setter) {
        return entryBuilder.startFloatField(Component.translatable("tellurium.feature."+key), value)
                .setDefaultValue(defaultValue)
                .setMin(min)
                .setMax(max)
                .setSaveConsumer(setter)
                .build();
    }
    private static ColorEntry makeColor(ConfigEntryBuilder entryBuilder, String key, int r, int g, int b, int dr, int dg, int db, Consumer<Integer[]> setter) {
        return entryBuilder.startColorField(Component.translatable("tellurium.feature."+key), new Color(r, g, b).getRGB() & 0xFFFFFF)
                .setDefaultValue(new Color(dr, dg, db).getRGB())
                .setTooltip(Component.translatable("tellurium.feature."+key+".tooltip"))
                .setSaveConsumer(newVal -> {
                    Color c = new Color(newVal);
                    setter.accept(new Integer[]{c.getRed(), c.getGreen(), c.getBlue()});
                })
                .build();
    }
    private static ColorEntry makeColorf(ConfigEntryBuilder entryBuilder, String key, float r, float g, float b, float dr, float dg, float db, Consumer<Float[]> setter) {
        return entryBuilder.startColorField(Component.translatable("tellurium.feature."+key), new Color(r, g, b).getRGB() & 0xFFFFFF)
                .setDefaultValue(new Color(dr, dg, db).getRGB())
                .setSaveConsumer(newVal -> {
                    Color c = new Color(newVal);
                    setter.accept(new Float[]{c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f});
                })
                .build();
    }
    private static <T extends Enum<T>>
    me.shedaniel.clothconfig2.gui.entries.EnumListEntry<T> makeEnum(
            ConfigEntryBuilder entryBuilder,
            String key,
            T value,
            T defaultValue,
            Consumer<T> setter
    ) {
        return entryBuilder.startEnumSelector(
                        Component.translatable("tellurium.feature." + key),
                        value.getDeclaringClass(),
                        value
                )
                .setDefaultValue(defaultValue)
                .setEnumNameProvider(v -> {
                    if (v instanceof EnchantmentDisplay ed) {
                        return ed.getText();
                    }
                    return Component.literal(v.name());
                })
                .setTooltip(Component.translatable("tellurium.feature." + key + ".tooltip"))
                .setSaveConsumer(setter)
                .build();
    }
}
