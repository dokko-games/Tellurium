package dev.dokko.tellurium.indicator;

import net.minecraft.resources.Identifier;

public class Indicator {
    public Identifier baseTexture;
    public Identifier overlayTexture;

    public Indicator(Identifier base, Identifier overlay) {
        this.baseTexture = base;
        this.overlayTexture = overlay;
    }
}
