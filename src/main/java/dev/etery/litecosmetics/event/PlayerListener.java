package dev.etery.litecosmetics.event;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.LiteCosmetics;
import dev.etery.litecosmetics.cosmetic.Taunt;
import dev.etery.litecosmetics.data.CosmeticPlayer;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.List;
import java.util.WeakHashMap;

public class PlayerListener implements Listener {
    private static final long cooldownTime = Duration.ofSeconds(12).toMillis();
    private final LiteCosmetics cosmetics;
    private final WeakHashMap<Player, Long> cooldown = new WeakHashMap<>();

    public PlayerListener(LiteCosmetics cosmetics) {
        this.cosmetics = cosmetics;
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getMaterial() != Material.DIAMOND) return;
        if (!(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) return;
        ItemMeta meta = event.getItem().getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.getLore();
        if (lore == null) return;
        if (lore.isEmpty()) return;
        if (!ChatColor.stripColor(lore.get(0)).equals("Litebridge")) return;

        Player p = event.getPlayer();

        Category<Taunt> taunts = cosmetics.category("taunts");
        CosmeticPlayer cosmeticPlayer = cosmetics.player(event.getPlayer());

        Taunt taunt = cosmeticPlayer.getSelected(taunts);

        if (taunt != null) {
            if (System.currentTimeMillis() - cooldown.getOrDefault(event.getPlayer(), 0L) < cooldownTime) {
                long playerCooldown = cooldown.getOrDefault(p, 0L);
                p.sendMessage(ChatColor.RED + "You're still in cooldown, please wait " + Duration.ofMillis(cooldownTime - (System.currentTimeMillis() - playerCooldown)).getSeconds() + " seconds");
                return;
            }
            taunt.use(event.getPlayer());
            cooldown.put(p, System.currentTimeMillis());
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
