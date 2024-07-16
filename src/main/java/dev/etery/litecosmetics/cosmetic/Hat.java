package dev.etery.litecosmetics.cosmetic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public class Hat implements Cosmetic {
    public final int price;
    public final String name;
    public final String id;
    public final ItemStack item;
    public final String description;

    public Hat(String id, String name, String description, Material item, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.item = new ItemStack(item);
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(displayName());
        this.item.setItemMeta(meta);
        this.price = price;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String displayName() {
        return ChatColor.translateAlternateColorCodes('&', this.name);
    }

    @Override
    public int price() {
        return this.price;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public ItemStack rawIcon() {
        return this.item.clone();
    }

    public void wear(Player player) {
        player.getInventory().setHelmet(this.icon());
    }

    public static Hat from(String id, MemorySection section) {
        String name = section.getString("display");
        String description = section.getString("description");
        String rawMaterial = section.getString("item");
        int price = section.getInt("price");

        Material material;
        try {
            material = Material.valueOf(rawMaterial.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            material = Material.GLASS;
        }

        return new Hat(id, name, description, material, price);
    }
}
