package dev.dokko.tellurium.indicator;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;

public class Indicators {
    public final ArrayList<Indicator> effects = new ArrayList<>();

    public void clear() {
        effects.clear();
    }

    public void add(Identifier iconTexture) {
        effects.add(new Indicator(iconTexture, null));
    }
    public void add(Indicator indicator) {
        effects.add(indicator);
    }

    public int size() {
        return effects.size();
    }

    public Indicator get(int i) {
        return effects.get(i);
    }
}
