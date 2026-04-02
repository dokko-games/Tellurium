package dev.dokko.tellurium.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
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
        config.setLowHealth2Indicator(file.getOrElse("indicators.lowHealth2", config.isLowHealth2Indicator()));
        config.setBurningIndicator(file.getOrElse("indicators.burning", config.isBurningIndicator()));
        config.setElytraIndicator(file.getOrElse("indicators.elytra", config.isElytraIndicator()));
        config.setRepotIndicator(file.getOrElse("indicators.repot", config.isRepotIndicator()));
        config.setLowDurabilityIndicator(file.getOrElse("indicators.lowDurability", config.isLowDurabilityIndicator()));
        config.setTotemIndicator(file.getOrElse("indicators.totem", config.isTotemIndicator()));

        // ===== Player =====
        config.setRemoveDeathAnimation(file.getOrElse("player.removeDeathAnimation", config.isRemoveDeathAnimation()));
        config.setPerfectReachSound(file.getOrElse("player.perfectReachSound", config.isPerfectReachSound()));
        
        // ===== Conditional Hitboxes =====
        config.setCrawlHitbox(file.getOrElse("hitboxes.crawl", config.isCrawlHitbox()));
        config.setElytraCrystalHitbox(file.getOrElse("hitboxes.elytraCrystal", config.isElytraCrystalHitbox()));
        config.setSpeedHitbox(file.getOrElse("hitboxes.speed", config.isSpeedHitbox()));
        config.setSpeedHitboxThreshold(getFloat("hitboxes.speed_threshold", config.getSpeedHitboxThreshold()));
        
        // ===== Entities =====
        config.setHideHitboxesForPassiveMobs(file.getOrElse("hitboxes.hide.passive", config.isHideHitboxesForPassiveMobs()));
        config.setHideHitboxesForNeutralMobs(file.getOrElse("hitboxes.hide.neutral", config.isHideHitboxesForNeutralMobs()));
        config.setHideHitboxesForHostileMobs(file.getOrElse("hitboxes.hide.hostile", config.isHideHitboxesForHostileMobs()));
        config.setHideHitboxesForPlayers(file.getOrElse("hitboxes.hide.player",  config.isHideHitboxesForPlayers()));
        config.setHideHitboxesForItems(file.getOrElse("hitboxes.hide.item", config.isHideHitboxesForItems()));

        // ===== Customization =====
        config.setDisableEyeLine(file.getOrElse("hitboxes.disable.eyeLine", config.isDisableEyeLine()));
        config.setDisableLookVector(file.getOrElse("hitboxes.disable.lookVector", config.isDisableLookVector()));
        config.setHitboxThickness(getFloat("hitboxes.thickness", config.getHitboxThickness()));
        
        // ===== Colors =====
        config.setPlayerR(getFloat("hitboxes.player.r", config.getPlayerR()));
        config.setPlayerG(getFloat("hitboxes.player.g", config.getPlayerG()));
        config.setPlayerB(getFloat("hitboxes.player.b", config.getPlayerB()));
        config.setPlayerA(getFloat("hitboxes.player.a", config.getPlayerA()));
        config.setHostileR(getFloat("hitboxes.hostile.r", config.getHostileR()));
        config.setHostileG(getFloat("hitboxes.hostile.g", config.getHostileG()));
        config.setHostileB(getFloat("hitboxes.hostile.b", config.getHostileB()));
        config.setHostileA(getFloat("hitboxes.hostile.a", config.getHostileA()));
        config.setNeutralR(getFloat("hitboxes.neutral.r", config.getNeutralR()));
        config.setNeutralG(getFloat("hitboxes.neutral.g", config.getNeutralG()));
        config.setNeutralB(getFloat("hitboxes.neutral.b", config.getNeutralB()));
        config.setNeutralA(getFloat("hitboxes.neutral.a", config.getNeutralA()));
        config.setPassiveR(getFloat("hitboxes.passive.r", config.getPassiveR()));
        config.setPassiveG(getFloat("hitboxes.passive.g", config.getPassiveG()));
        config.setPassiveB(getFloat("hitboxes.passive.b", config.getPassiveB()));
        config.setPassiveA(getFloat("hitboxes.passive.a", config.getPassiveA()));
        config.setItemR(getFloat("hitboxes.item.r", config.getItemR()));
        config.setItemG(getFloat("hitboxes.item.g", config.getItemG()));
        config.setItemB(getFloat("hitboxes.item.b", config.getItemB()));
        config.setItemA(getFloat("hitboxes.item.a", config.getItemA()));

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
        file.set("indicators.lowHealth2", config.isLowHealth2Indicator());
        file.set("indicators.burning", config.isBurningIndicator());
        file.set("indicators.elytra", config.isElytraIndicator());
        file.set("indicators.repot", config.isRepotIndicator());
        file.set("indicators.lowDurability", config.isLowDurabilityIndicator());
        file.set("indicators.totem", config.isTotemIndicator());

        // ===== Player =====
        file.set("player.removeDeathAnimation", config.isRemoveDeathAnimation());
        file.set("player.perfectReachSound", config.isPerfectReachSound());

        // ===== Conditional Hitboxes =====
        file.set("hitboxes.crawl", config.isCrawlHitbox());
        file.set("hitboxes.elytraCrystal", config.isElytraCrystalHitbox());
        file.set("hitboxes.speed", config.isSpeedHitbox());
        file.set("hitboxes.speed_threshold", (double)config.getSpeedHitboxThreshold());

        // ===== Entities =====
        file.set("hitboxes.hide.passive", config.isHideHitboxesForPassiveMobs());
        file.set("hitboxes.hide.neutral", config.isHideHitboxesForNeutralMobs());
        file.set("hitboxes.hide.hostile", config.isHideHitboxesForHostileMobs());
        file.set("hitboxes.hide.player",  config.isHideHitboxesForPlayers());
        file.set("hitboxes.hide.item", config.isHideHitboxesForItems());

        // ===== Customization =====
        file.set("hitboxes.disable.eyeLine", config.isDisableEyeLine());
        file.set("hitboxes.disable.lookVector", config.isDisableLookVector());
        file.set("hitboxes.thickness", (double)config.getHitboxThickness());

        // ===== Colors =====
        file.set("hitboxes.player.r", config.getPlayerR());
        file.set("hitboxes.player.g", config.getPlayerG());
        file.set("hitboxes.player.b", config.getPlayerB());
        file.set("hitboxes.player.a", config.getPlayerA());
        file.set("hitboxes.hostile.r", config.getHostileR());
        file.set("hitboxes.hostile.g", config.getHostileG());
        file.set("hitboxes.hostile.b", config.getHostileB());
        file.set("hitboxes.hostile.a", config.getHostileA());
        file.set("hitboxes.neutral.r", config.getNeutralR());
        file.set("hitboxes.neutral.g", config.getNeutralG());
        file.set("hitboxes.neutral.b", config.getNeutralB());
        file.set("hitboxes.neutral.a", config.getNeutralA());
        file.set("hitboxes.passive.r", config.getPassiveR());
        file.set("hitboxes.passive.g", config.getPassiveG());
        file.set("hitboxes.passive.b", config.getPassiveB());
        file.set("hitboxes.passive.a", config.getPassiveA());
        file.set("hitboxes.item.r", config.getItemR());
        file.set("hitboxes.item.g", config.getItemG());
        file.set("hitboxes.item.b", config.getItemB());
        file.set("hitboxes.item.a", config.getItemA());

        file.save();
    }
    private static float getFloat(String key, float def) {
        return ((Number) file.getOrElse(key, def)).floatValue();
    }
}