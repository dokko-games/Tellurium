package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Tellurium;
import dev.dokko.tellurium.config.EnchantmentDisplay;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Language.class)
public class LanguageMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void interceptEnchantmentLevelKey(String key, CallbackInfoReturnable<String> cir) {
        EnchantmentDisplay display = Tellurium.getManager().getConfig().getEnchantmentDisplay();
        if (display != EnchantmentDisplay.DEFAULT && key.startsWith("enchantment.level.")) {
            String levelStr = key.substring("enchantment.level.".length());

            try {
                int level = Integer.parseInt(levelStr);

                switch (display) {
                    case DECIMAL -> cir.setReturnValue(levelStr);
                    case ROMAN -> cir.setReturnValue(toRoman(level));
                    case BOTH -> {
                        String roman = toRoman(level);
                        cir.setReturnValue(roman + " (" + levelStr + ")");
                    }
                }
            } catch (Exception e) {
                // fallback — not a valid number, let vanilla handle it
            }
        }
    }
    @Unique
    private String toRoman(int number) {
        if (number <= 0 || number > 3999) return String.valueOf(number); // fallback
        StringBuilder result = new StringBuilder();

        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"M", "CM", "D", "CD", "C","XC","L","XL","X","IX","V","IV","I"};

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                result.append(numerals[i]);
            }
        }

        return result.toString();
    }
}
