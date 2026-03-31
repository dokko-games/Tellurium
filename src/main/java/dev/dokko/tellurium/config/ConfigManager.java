package dev.dokko.tellurium.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ConfigManager {

    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("tellurium.toml");

    private static CommentedFileConfig file;

    public static Config load() {
        file = CommentedFileConfig.builder(PATH)
                .autosave()
                .sync()
                .build();

        file.load();

        Config config = new Config();

        // ===== General =====
        config.setInvertSprint(file.getOrElse("general.invertSprint", config.isInvertSprint()));
        config.setGuiSneak(file.getOrElse("general.guiSneak", config.isGuiSneak()));
        config.setStoreChatMessage(file.getOrElse("general.storeChatMessage", config.isStoreChatMessage()));

        String display = file.getOrElse("general.enchantmentDisplay", config.getEnchantmentDisplay().name());
        config.setEnchantmentDisplay(EnchantmentDisplay.valueOf(display));

        // ===== UI =====
        config.setChatTextBoxOpacity(file.getOrElse("ui.chatTextBoxOpacity", config.getChatTextBoxOpacity()));

        // ===== Indicators =====
        config.setIndicatorSize(file.getOrElse("indicators.size", config.getIndicatorSize()));
        config.setIndicatorOffset(file.getOrElse("indicators.offset", config.getIndicatorOffset()));

        config.setShieldStunIndicator(file.getOrElse("indicators.shieldStun", config.isShieldStunIndicator()));
        config.setMaceSlowFallIndicator(file.getOrElse("indicators.maceSlowFall", config.isMaceSlowFallIndicator()));
        config.setLowHealthIndicator(file.getOrElse("indicators.lowHealth", config.isLowHealthIndicator()));
        config.setBurningIndicator(file.getOrElse("indicators.burning", config.isBurningIndicator()));
        config.setElytraIndicator(file.getOrElse("indicators.elytra", config.isElytraIndicator()));
        config.setRepotIndicator(file.getOrElse("indicators.repot", config.isRepotIndicator()));
        config.setLowDurabilityIndicator(file.getOrElse("indicators.lowDurability", config.isLowDurabilityIndicator()));
        config.setTotemIndicator(file.getOrElse("indicators.totem", config.isTotemIndicator()));

        // ===== Player =====
        config.setRemoveDeathAnimation(file.getOrElse("player.removeDeathAnimation", config.isRemoveDeathAnimation()));
        config.setPerfectReachSound(file.getOrElse("player.perfectReachSound", config.isPerfectReachSound()));

        return config;
    }

    public static void save(Config config) {
        if (file == null) return;

        // ===== General =====
        file.set("general.invertSprint", config.isInvertSprint());
        file.set("general.guiSneak", config.isGuiSneak());
        file.set("general.storeChatMessage", config.isStoreChatMessage());
        file.set("general.enchantmentDisplay", config.getEnchantmentDisplay().name());

        // ===== UI =====
        file.set("ui.chatTextBoxOpacity", config.getChatTextBoxOpacity());

        // ===== Indicators =====
        file.set("indicators.size", config.getIndicatorSize());
        file.set("indicators.offset", config.getIndicatorOffset());

        file.set("indicators.shieldStun", config.isShieldStunIndicator());
        file.set("indicators.maceSlowFall", config.isMaceSlowFallIndicator());
        file.set("indicators.lowHealth", config.isLowHealthIndicator());
        file.set("indicators.burning", config.isBurningIndicator());
        file.set("indicators.elytra", config.isElytraIndicator());
        file.set("indicators.repot", config.isRepotIndicator());
        file.set("indicators.lowDurability", config.isLowDurabilityIndicator());
        file.set("indicators.totem", config.isTotemIndicator());

        // ===== Player =====
        file.set("player.removeDeathAnimation", config.isRemoveDeathAnimation());
        file.set("player.perfectReachSound", config.isPerfectReachSound());

        file.save();
    }
}