package dev.dokko.tellurium.config;

import dev.dokko.tellurium.Tellurium;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;

public enum EnchantmentDisplay implements TranslatableOption {
    DEFAULT("Normal", 0),
    DECIMAL("Decimal", 1),
    ROMAN("Roman", 2),
    BOTH("Both", 3);
    private final String id;
    private final int iid;

    EnchantmentDisplay(String id, int iid) {
        this.id = id;
        this.iid = iid;
    }

    @Override
    public int getId() {
        return iid;
    }

    @Override
    public String getTranslationKey() {
        return Tellurium.MOD_ID +".feature.enchantmentDisplay." + id;
    }

    public String getStrId() {
        return id;
    }

    @Override
    public Text getText() {
        return TranslatableOption.super.getText();
    }
}
