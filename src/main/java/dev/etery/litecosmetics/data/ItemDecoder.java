package dev.etery.litecosmetics.data;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public class ItemDecoder {
    public static ItemStack decode(MemorySection section, String itemKey) {
        String rawMaterial = section.getString(itemKey);
        int data = section.getInt("item_data");
        boolean enchanted = section.getBoolean("item_glow", false);
        Material material;
        try {
            material = Material.valueOf(rawMaterial.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Couldn't parse Material \"" + rawMaterial.toUpperCase(Locale.ROOT) + "\"");
        }
        ItemStack stack = new ItemStack(material, 1, (short) data);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        if (enchanted) stack.addUnsafeEnchantment(Enchantment.LUCK, 1);
        return stack;
    }
}
