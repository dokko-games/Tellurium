package dev.dokko.tellurium.util;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.uku3lig.ukulib.config.option.WidgetCreator;

public class SimpleTooltipButton implements WidgetCreator {
    private final Text text;
    private final ButtonWidget.PressAction action;
    private final Text tooltip;

    public SimpleTooltipButton(Text key, ButtonWidget.PressAction action, Text tooltip) {
        this.text = key;
        this.action = action;
        this.tooltip = tooltip;
    }

    public SimpleTooltipButton(String key, ButtonWidget.PressAction action, Text tooltip) {
        this((Text)Text.translatable(key), action, tooltip);
    }

    public ClickableWidget createWidget(int x, int y, int width, int height) {
        return ButtonWidget.builder(this.text, this.action).dimensions(x, y, width, height).tooltip(Tooltip.of(tooltip)).build();
    }
}
