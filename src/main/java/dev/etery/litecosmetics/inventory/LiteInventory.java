package dev.etery.litecosmetics.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LiteInventory {
    private static final HashMap<Inventory, LiteInventory> inventories = new HashMap<>();

    public static LiteInventory create(Inventory inventory, Consumer<InventoryClickEvent> interaction) {
        LiteInventory liteInventory = new LiteInventory(interaction);
        inventories.put(inventory, liteInventory);
        return liteInventory;
    }

    public static LiteInventory of(Inventory inventory) {
        return inventories.get(inventory);
    }

    private final Consumer<InventoryClickEvent> interactions;

    private LiteInventory(Consumer<InventoryClickEvent> interactions) {
        this.interactions = interactions;
    }

    public void handleInteract(InventoryInteractEvent event) {
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }

    public void handleClick(InventoryClickEvent event) {
        this.interactions.accept(event);
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
    }
}
