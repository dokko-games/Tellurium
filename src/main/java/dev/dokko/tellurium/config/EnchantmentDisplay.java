package dev.dokko.tellurium.config;

import dev.dokko.tellurium.Tellurium;
import net.minecraft.network.chat.Component;

public enum EnchantmentDisplay {
    DEFAULT("normal"),
    DECIMAL("decimal"),
    ROMAN("roman"),
    BOTH("both");

    private final String id;

    EnchantmentDisplay(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        return Tellurium.MOD_ID + ".feature.enchantmentDisplay." + id;
    }

    public Component getText() {
        return Component.translatable(getTranslationKey());
    }
}