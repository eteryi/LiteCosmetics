package dev.etery.litecosmetics.cosmetic;

import dev.etery.litecosmetics.data.ItemDecoder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Hat implements Cosmetic {
    public final int price;
    public final String name;
    public final String id;
    public final ItemStack item;
    public final String description;

    public Hat(String id, String name, String description, ItemStack item, int price) {
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
        ItemStack stack = ItemDecoder.decode(section, "item");
        int price = section.getInt("price");

        return new Hat(id, name, description, stack, price);
    }
}
