package dev.etery.litecosmetics.inventory;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InventoryBuilder {
    private final int rows;
    private final String title;
    private final ItemStack[] items;
    private Consumer<InventoryClickEvent> interactions;

    public InventoryBuilder(String title, int rows) {
        this.rows = rows;
        this.title = title;
        this.items = new ItemStack[rows * 9];
        this.interactions = (e) -> {};
    }

    public InventoryBuilder setItem(int slot, ItemStack itemStack) {
        this.items[slot] = itemStack;
        return this;
    }

    public InventoryBuilder setInteraction(Consumer<InventoryClickEvent> eventConsumer) {
        this.interactions = eventConsumer;
        return this;
    }

    public Inventory build(Player player) {
        Inventory inventory = Bukkit.createInventory(player, rows * 9, title);
        for (int i = 0; i < this.items.length; i++) {
            if (items[i] == null) continue;
            inventory.setItem(i, items[i]);
        }
        LiteInventory.create(inventory, this.interactions);
        return inventory;
    }
}
