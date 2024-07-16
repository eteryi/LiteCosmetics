package dev.etery.litecosmetics.event;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.LiteCosmetics;
import dev.etery.litecosmetics.cosmetic.Taunt;
import dev.etery.litecosmetics.data.CosmeticPlayer;
import dev.etery.litecosmetics.inventory.InventoryBuilder;
import dev.etery.litecosmetics.inventory.LiteInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PlayerListener implements Listener {
    private final LiteCosmetics cosmetics;

    public PlayerListener(LiteCosmetics cosmetics) {
        this.cosmetics = cosmetics;
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getMaterial() != Material.DIAMOND) return;
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.getLore();
        if (lore == null) return;
        if (lore.isEmpty()) return;
        if (!ChatColor.stripColor(lore.get(0)).equals("Litebridge")) return;

        Category<Taunt> taunts = cosmetics.category("taunts");
        CosmeticPlayer cosmeticPlayer = cosmetics.player(event.getPlayer());

        Taunt taunt = cosmeticPlayer.getSelected(taunts);

        if (event.getAction() == Action.LEFT_CLICK_AIR && taunt != null) {
            taunt.use(event.getPlayer());
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            InventoryBuilder builder = new InventoryBuilder("Taunts", 5);
            int i = 0;
            List<Taunt> tauntView = taunts.boughtCosmetics(cosmeticPlayer);
            for (Taunt t : tauntView) {
                builder.setItem(i, t.icon());
                i++;
            }
            builder.setInteraction((e) -> {
               if (e.getSlot() >= 0 && e.getSlot() < tauntView.size()) {
                   Taunt select = tauntView.get(e.getSlot());
                   event.getPlayer().sendMessage(ChatColor.GRAY + " > Selected " + ChatColor.RESET + select.displayName());
                   cosmeticPlayer.select(taunts, select);
                   event.getPlayer().closeInventory();
               }
            });

            event.getPlayer().openInventory(builder.build(event.getPlayer()));
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (LiteInventory.of(event.getClickedInventory()) != null) {
            LiteInventory.of(event.getClickedInventory()).handleClick(event);
        }
    }

    @EventHandler
    public void inventoryInteract(InventoryInteractEvent event) {
        if (LiteInventory.of(event.getInventory()) != null) {
            LiteInventory.of(event.getInventory()).handleInteract(event);
        }
    }
}
