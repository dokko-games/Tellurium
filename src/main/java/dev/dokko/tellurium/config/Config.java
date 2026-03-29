package dev.dokko.tellurium.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Config implements Serializable {
    // General
    private boolean invertSprint = false;
    private boolean guiSneak = true;
    private boolean storeChatMessage = true;
    private EnchantmentDisplay enchantmentDisplay = EnchantmentDisplay.DECIMAL;
    // UI
    private int chatTextBoxOpacity = 0x80;
    // Indicators
    private int indicatorSize = 10;
    private int indicatorOffset = 15;
    private boolean shieldStunIndicator = true;
    private boolean maceSlowFallIndicator = true;
    private boolean lowHealthIndicator = false;
    private boolean burningIndicator = true;
    private boolean elytraIndicator = false;
    private boolean repotIndicator = false;
    private boolean lowDurabilityIndicator = true;
    private boolean totemIndicator = false;
    // Player
    private boolean removeDeathAnimation = false;
    private boolean perfectReachSound = true;
    // Hitboxes
    private boolean renderHitboxes;
    // Conditional Hitboxes
    private boolean crawlHitbox = false;
    private boolean elytraCrystalHitbox = true;
    private boolean speedHitbox = true;
    private double speedHitboxThreshold = 1.4f;

    private boolean hideHitboxesForPlayers = false;
    private boolean hideHitboxesForPassiveMobs = true;
    private boolean hideHitboxesForNeutralMobs = true;
    private boolean hideHitboxesForHostileMobs = false;
    // Customization
    private boolean disableEyeLine = true;
    private boolean disableLookVector = true;
    private float hitboxFillOpacity = 0.5f;
    // Colors
    private float playerR = 1, playerG = 1, playerB = 1, playerA = 1;
    private float passiveR = .2f, passiveG = 1, passiveB = .2f, passiveA = 1;
    private float neutralR = 1f, neutralG = 1, neutralB = .2f, neutralA = 1;
    private float hostileR = 1f, hostileG = .2f, hostileB = .2f, hostileA = 1;

}
