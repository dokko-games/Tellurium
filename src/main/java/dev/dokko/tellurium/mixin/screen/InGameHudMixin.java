package dev.dokko.tellurium.mixin.screen;

import dev.dokko.tellurium.Tellurium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private static final int ICON_DISTANCE = 4;
    @Unique
    private static final int MAX_ICONS_PER_ROW = 5;
    @Unique
    private static final int ROW_DISTANCE = 2;
    @Unique
    private static final ArrayList<Identifier> effects = new ArrayList<>();
    @Inject(method = "render", at = @At("TAIL"))
    private void renderIndicators(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        effects.clear();

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.options.hudHidden) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        ItemStack mainHand = client.player.getMainHandStack();
        ItemStack offHand = client.player.getOffHandStack();

        renderIndicators(context, mainHand, client, offHand, screenWidth, screenHeight);
    }

    @Unique
    private void renderIndicators(DrawContext context, ItemStack mainHand, MinecraftClient client, ItemStack offHand, int screenWidth, int screenHeight) {
        if (Tellurium.getManager().getConfig().isLowHealthIndicator() && client.player.getHealth() <= 6){
            Identifier iconTexture = Identifier.of(Tellurium.MOD_ID, "textures/icon/stat/low_health.png");
            effects.add(iconTexture);
        }
        boolean holdingShield = false;
        boolean stunned = false;
        ItemCooldownManager cooldownManager = client.player.getItemCooldownManager();
        if(mainHand.isOf(Items.SHIELD)){
            holdingShield = true;
            stunned = cooldownManager.isCoolingDown(mainHand);
        }
        else if(offHand.isOf(Items.SHIELD)){
            holdingShield = true;
            stunned = cooldownManager.isCoolingDown(offHand);
        }
        if(Tellurium.getManager().getConfig().isShieldStunIndicator() &&holdingShield && stunned) {
            Identifier iconTexture = Identifier.of(Tellurium.MOD_ID, "textures/icon/stat/shield_stun.png");
            effects.add(iconTexture);
        }
        boolean hasStrength = client.player.hasStatusEffect(StatusEffects.STRENGTH);
        boolean hasSpeed = client.player.hasStatusEffect(StatusEffects.SPEED);
        boolean strengthRunningOut = false, speedRunningOut = false;
        if(hasStrength){
            StatusEffectInstance strength = client.player.getStatusEffect(StatusEffects.STRENGTH);
            strengthRunningOut = !strength.isInfinite() &&
                    strength.getDuration() <= 200;
        }
        if(hasSpeed){
            StatusEffectInstance speed = client.player.getStatusEffect(StatusEffects.SPEED);
            speedRunningOut = !speed.isInfinite() &&
                    speed.getDuration() <= 200;
        }
        if (Tellurium.getManager().getConfig().isRepotIndicator()){
            if((hasStrength && strengthRunningOut) || (hasSpeed && speedRunningOut)){
                Identifier iconTexture = Identifier.of(Tellurium.MOD_ID, "textures/icon/stat/repot.png");
                effects.add(iconTexture);
            }
        }
        if (Tellurium.getManager().getConfig().isTotemIndicator() && !offHand.isOf(Items.TOTEM_OF_UNDYING)
                && hasTotemInInventory(client.player)) {
            Identifier iconTexture = Identifier.of("minecraft", "textures/item/totem_of_undying.png");
            effects.add(iconTexture);
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armor = client.player.getEquippedStack(slot);

                if (armor.isDamageable() &&
                        armor.getMaxDamage() - armor.getDamage() <= 50) {

                    effects.add(Identifier.of(Tellurium.MOD_ID, "textures/icon/stat/low_armor.png"));
                    break;
                }
            }
        }
        if(Tellurium.getManager().getConfig().isBurningIndicator() && client.player.isOnFire()) {
            Identifier iconTexture = Identifier.of(Tellurium.MOD_ID, "textures/icon/stat/burning.png");
            effects.add(iconTexture);
        }
        boolean holdingMace = mainHand.isOf(Items.MACE);
        boolean hasSlowFalling = client.player.hasStatusEffect(StatusEffects.SLOW_FALLING);
        if (holdingMace && hasSlowFalling && Tellurium.getManager().getConfig().isMaceSlowFallIndicator()) {
            Identifier iconTexture = Identifier.of(Tellurium.MOD_ID, "textures/icon/stat/mace_slowfall.png");
            effects.add(iconTexture);
        }
        if(Tellurium.getManager().getConfig().isElytraIndicator() && client.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            Identifier iconTexture = Identifier.of("minecraft", "textures/item/elytra.png");
            effects.add(iconTexture);
        }

        renderEffects(context, screenWidth, screenHeight);
    }

    @Unique
    private void renderEffects(DrawContext context, int screenWidth, int screenHeight) {
        int ICON_SIZE = Tellurium.getManager().getConfig().getIndicatorSize();

        int totalIcons = effects.size();

        for (int i = 0; i < totalIcons; i++) {
            Identifier iconTexture = effects.get(i);

            int row = i / MAX_ICONS_PER_ROW;
            int col = i % MAX_ICONS_PER_ROW;

            int iconsInThisRow = Math.min(MAX_ICONS_PER_ROW, totalIcons - row * MAX_ICONS_PER_ROW);
            int rowWidth = iconsInThisRow * ICON_SIZE + (iconsInThisRow - 1) * ICON_DISTANCE;
            int startX = screenWidth / 2 - rowWidth / 2;

            int iconX = startX + col * (ICON_SIZE + ICON_DISTANCE);
            int iconY = screenHeight / 2 + Tellurium.getManager().getConfig().getIndicatorOffset() + row * (ICON_SIZE + ROW_DISTANCE);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, iconTexture, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }
    }
    @Unique
    private boolean hasTotemInInventory(PlayerEntity player) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.getItem() == Items.TOTEM_OF_UNDYING && !stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
