package dev.dokko.tellurium.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Map;

public class EnchantmentUtil {
    public static boolean hasEnchantment(ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        for (Map.Entry<RegistryEntry<Enchantment>, Integer> entry :
                EnchantmentHelper.getEnchantments(itemStack).getEnchantmentEntries()) {
            RegistryEntry<Enchantment> key = entry.getKey();
            if (key.matchesKey(enchantment)) {
                return true;
            }
        }
        return false;
    }
    public static int getLevel(ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        for (Map.Entry<RegistryEntry<Enchantment>, Integer> entry :
                EnchantmentHelper.getEnchantments(itemStack).getEnchantmentEntries()) {
            RegistryEntry<Enchantment> key = entry.getKey();
            if (key.matchesKey(enchantment)) {
                return EnchantmentHelper.getLevel(key, itemStack);
            }
        }
        return -1;
    }
}