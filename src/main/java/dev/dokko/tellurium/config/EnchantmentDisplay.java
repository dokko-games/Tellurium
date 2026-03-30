package dev.dokko.tellurium.config;

import dev.dokko.tellurium.Tellurium;
import net.minecraft.network.chat.Component;
import net.uku3lig.ukulib.config.option.StringTranslatable;

public enum EnchantmentDisplay implements StringTranslatable {
    DEFAULT("Normal"),
    DECIMAL("Decimal"),
    ROMAN("Roman"),
    BOTH("Both");
    private final String id;

    EnchantmentDisplay(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return "id";
    }

    @Override
    public String getTranslationKey() {
        return Tellurium.MOD_ID +".feature.enchantmentDisplay." + id;
    }

    public String getStrId() {
        return id;
    }

    @Override
    public Component getText() {
        return StringTranslatable.super.getText();
    }
}
