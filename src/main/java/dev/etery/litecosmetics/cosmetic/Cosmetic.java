package dev.etery.litecosmetics.cosmetic;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public interface Cosmetic {
    String id();
    String displayName();
    int price();
    String description();
    ItemStack rawIcon();
    default ItemStack icon() {
        ItemStack stack = rawIcon().clone();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', displayName()));

        List<String> lore = new ArrayList<>();
        String rawDescription = ChatColor.translateAlternateColorCodes('&', description());
        String[] loreDescription = rawDescription.split("\n");
        for (String s : loreDescription) {
            lore.add(ChatColor.RESET.toString() + ChatColor.GRAY + s);
        }
        lore.add(" ");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + this.id());

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);

        return stack;
    }
}
