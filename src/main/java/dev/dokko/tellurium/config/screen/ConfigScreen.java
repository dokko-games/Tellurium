package dev.dokko.tellurium.config.screen;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.Config;
import dev.dokko.tellurium.config.EnchantmentDisplay;
import dev.dokko.tellurium.config.screen.hitbox.HitboxesScreen;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.uku3lig.ukulib.config.option.*;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

public class ConfigScreen extends AbstractConfigScreen<Config> {
    public ConfigScreen(Screen parent) {
        super(Tellurium.MOD_ID+".config.screen", parent, Tellurium.getManager());
    }

    @Override
    protected WidgetCreator[] getWidgets(Config config) {
        return new WidgetCreator[]{
                makeBoolean("invertSprint", config.isInvertSprint(), config::setInvertSprint),
                makeBoolean("guiSneak", config.isGuiSneak(), config::setGuiSneak),
                makeBoolean("storeChatMessage", config.isStoreChatMessage(), config::setStoreChatMessage),
                makeInt("chatTextBoxOpacity", config.getChatTextBoxOpacity(), config::setChatTextBoxOpacity, 0, 255, 1),
                makeBoolean("removeDeathAnimations", config.isRemoveDeathAnimation(), config::setRemoveDeathAnimation),
                makeBoolean("perfectReachSound", config.isPerfectReachSound(), config::setPerfectReachSound),
                new ScreenOpenButton(Tellurium.MOD_ID+".indicators", IndicatorsScreen::new),
                CyclingOption.ofTranslatable(Tellurium.MOD_ID+".feature.enchantmentDisplay", List.of(EnchantmentDisplay.values()),
                        config.getEnchantmentDisplay(), config::setEnchantmentDisplay, makeTooltip(Tellurium.MOD_ID+".feature.enchantmentDisplay.tooltip")),
                new ScreenOpenButton(Tellurium.MOD_ID+".hitboxes", HitboxesScreen::new),
        };
    }
    public static <T> SimpleOption.TooltipFactory<T> makeTooltip(String key) {
        return value -> Tooltip.of(Text.translatable(key));
    }
    public static CyclingOption<Boolean> makeBoolean(String key, boolean init, Consumer<Boolean> setter){
        return CyclingOption.ofBoolean(Tellurium.MOD_ID+".feature."+key, init, setter, makeTooltip(Tellurium.MOD_ID+".feature."+key+".tooltip"));
    }
    public static CyclingOption<Boolean> makeNTBoolean(String key, boolean init, Consumer<Boolean> setter){
        return CyclingOption.ofBoolean(Tellurium.MOD_ID+".feature."+key, init, setter);
    }
    public static IntSliderOption makeInt(String key, int init, IntConsumer setter, int min, int max, int step){
        return new IntSliderOption(Tellurium.MOD_ID+".feature."+key, init, setter, IntSliderOption.DEFAULT_INT_TO_TEXT, min, max, step, makeTooltip(Tellurium.MOD_ID+".feature."+key+".tooltip"));
    }
    public static SliderOption makeDouble(String key, double init, DoubleConsumer setter, double min, double max, double step){
        return new SliderOption(Tellurium.MOD_ID+".feature."+key, init, setter, SliderOption.DEFAULT_VALUE_TO_TEXT, min, max, step, makeTooltip(Tellurium.MOD_ID+".feature."+key+".tooltip"));
    }
    public static SliderOption makeFloat(String key, float init, FloatConsumer setter, float min, float max, float step){
        return new SliderOption(Tellurium.MOD_ID+".feature." + key, init, value -> setter.accept((float) value), SliderOption.DEFAULT_VALUE_TO_TEXT, min, max, step, makeTooltip(Tellurium.MOD_ID+".feature." + key + ".tooltip"));
    }
}
