package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Tellurium;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Gui.class)
public class GuiMixin {
    @Unique
    private static final int ICON_DISTANCE = 4;
    @Unique
    private static final int MAX_ICONS_PER_ROW = 5;
    @Unique
    private static final int ROW_DISTANCE = 2;
    @Unique
    private static final ArrayList<Identifier> effects = new ArrayList<>();
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderIndicators(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        effects.clear();

        Minecraft client = Minecraft.getInstance();

        if (client.player == null || client.options.hideGui) return;

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        ItemStack mainHand = client.player.getMainHandItem();
        ItemStack offHand = client.player.getOffhandItem();

        renderIndicators(graphics, mainHand, client, offHand, screenWidth, screenHeight);
    }

    @Unique
    private void renderIndicators(GuiGraphicsExtractor guiGraphics, ItemStack mainHand, Minecraft client, ItemStack offHand, int screenWidth, int screenHeight) {
        if (Tellurium.getConfig().isLowHealthIndicator() && client.player.getHealth() <= 6){
            Identifier iconTexture = Identifier.fromNamespaceAndPath(Tellurium.MOD_ID, "textures/icon/stat/low_health.png");
            effects.add(iconTexture);
        }
        if (Tellurium.getConfig().isLowHealth2Indicator() && client.player.getHealth() <= 13){
            if(client.player.getHealth() > 6){
                Identifier iconTexture = Identifier.fromNamespaceAndPath(Tellurium.MOD_ID, "textures/icon/stat/low_health_2.png");
                effects.add(iconTexture);
            }
        }
        boolean holdingShield = false;
        boolean stunned = false;
        ItemCooldowns cooldownManager = client.player.getCooldowns();
        if(mainHand.is(Items.SHIELD)){
            holdingShield = true;
            stunned = cooldownManager.isOnCooldown(mainHand);
        }
        else if(offHand.is(Items.SHIELD)){
            holdingShield = true;
            stunned = cooldownManager.isOnCooldown(offHand);
        }
        if(Tellurium.getConfig().isShieldStunIndicator() &&holdingShield && stunned) {
            Identifier iconTexture = Identifier.fromNamespaceAndPath(Tellurium.MOD_ID, "textures/icon/stat/shield_stun.png");
            effects.add(iconTexture);
        }
        boolean hasStrength = client.player.hasEffect(MobEffects.STRENGTH);
        boolean hasSpeed = client.player.hasEffect(MobEffects.SPEED);
        boolean strengthRunningOut = false, speedRunningOut = false;
        if(hasStrength){
            MobEffectInstance strength = client.player.getEffect(MobEffects.STRENGTH);
            strengthRunningOut = !strength.isInfiniteDuration() &&
                    strength.getDuration() <= 200;
        }
        if(hasSpeed){
            MobEffectInstance speed = client.player.getEffect(MobEffects.SPEED);
            speedRunningOut = !speed.isInfiniteDuration() &&
                    speed.getDuration() <= 200;
        }
        if (Tellurium.getConfig().isRepotIndicator()){
            if((hasStrength && strengthRunningOut) || (hasSpeed && speedRunningOut)){
                Identifier iconTexture = Identifier.fromNamespaceAndPath(Tellurium.MOD_ID, "textures/icon/stat/repot.png");
                effects.add(iconTexture);
            }
        }
        if (Tellurium.getConfig().isTotemIndicator() && !offHand.is(Items.TOTEM_OF_UNDYING)
                && hasTotemInInventory(client.player)) {
            Identifier iconTexture = Identifier.fromNamespaceAndPath("minecraft", "textures/item/totem_of_undying.png");
            effects.add(iconTexture);
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armor = client.player.getItemBySlot(slot);

                if (armor.isDamageableItem() &&
                        armor.getMaxDamage() - armor.getDamageValue() <= 50) {

                    effects.add(Identifier.fromNamespaceAndPath(Tellurium.MOD_ID, "textures/icon/stat/low_armor.png"));
                    break;
                }
            }
        }
        if(Tellurium.getConfig().isBurningIndicator() && client.player.isOnFire()) {
            Identifier iconTexture = Identifier.fromNamespaceAndPath(Tellurium.MOD_ID, "textures/icon/stat/burning.png");
            effects.add(iconTexture);
        }
        boolean holdingMace = mainHand.is(Items.MACE);
        boolean hasSlowFalling = client.player.hasEffect(MobEffects.SLOW_FALLING);
        if (holdingMace && hasSlowFalling && Tellurium.getConfig().isMaceSlowFallIndicator()) {
            Identifier iconTexture = Identifier.fromNamespaceAndPath(Tellurium.MOD_ID, "textures/icon/stat/mace_slowfall.png");
            effects.add(iconTexture);
        }
        if(Tellurium.getConfig().isElytraIndicator() && client.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            Identifier iconTexture = Identifier.fromNamespaceAndPath("minecraft", "textures/item/elytra.png");
            effects.add(iconTexture);
        }

        renderEffects(guiGraphics, screenWidth, screenHeight);
    }

    @Unique
    private void renderEffects(GuiGraphicsExtractor guiGraphics, int screenWidth, int screenHeight) {
        int ICON_SIZE = Tellurium.getConfig().getIndicatorSize();

        int totalIcons = effects.size();

        for (int i = 0; i < totalIcons; i++) {
            Identifier iconTexture = effects.get(i);

            int row = i / MAX_ICONS_PER_ROW;
            int col = i % MAX_ICONS_PER_ROW;

            int iconsInThisRow = Math.min(MAX_ICONS_PER_ROW, totalIcons - row * MAX_ICONS_PER_ROW);
            int rowWidth = iconsInThisRow * ICON_SIZE + (iconsInThisRow - 1) * ICON_DISTANCE;
            int startX = screenWidth / 2 - rowWidth / 2;

            int iconX = startX + col * (ICON_SIZE + ICON_DISTANCE);
            int iconY = screenHeight / 2 + Tellurium.getConfig().getIndicatorOffset() + row * (ICON_SIZE + ROW_DISTANCE);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, iconTexture, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }
    }
    @Unique
    private boolean hasTotemInInventory(Player player) {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.getItem() == Items.TOTEM_OF_UNDYING && !stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
