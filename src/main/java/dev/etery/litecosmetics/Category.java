package dev.etery.litecosmetics;

import dev.etery.litecosmetics.cosmetic.Cosmetic;
import dev.etery.litecosmetics.data.CosmeticPlayer;
import dev.etery.litecosmetics.inventory.InventoryBuilder;
import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Category<T extends Cosmetic> {
    private final HashMap<String, T> cosmetics;
    public final String id;
    private final String display;
    private final String description;
    private final ItemStack icon;

    public Category(String id, String displayName, String description, ItemStack icon) {
        this.id = id;
        this.display = displayName;
        this.icon = icon;
        this.description = description;
        this.cosmetics = new HashMap<>();
    }

    public void add(T cosmetic) {
        this.cosmetics.put(cosmetic.id(), cosmetic);
    }

    public String getDisplayName() {
        return ChatColor.translateAlternateColorCodes('&', this.display);
    }
    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', this.description);
    }

    public ItemStack icon() {
        ItemStack item = icon.clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + getDisplayName());

        String raw = getDescription();
        String[] strings = raw.split("\n");
        ArrayList<String> lore = new ArrayList<>();

        for (String s : strings) {
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + s);
        }
        lore.add(" ");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + id);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public T get(String id) {
        return cosmetics.get(id);
    }

    public List<T> view() {
        return new ArrayList<>(this.cosmetics.values());
    }

    public List<T> boughtCosmetics(CosmeticPlayer player) {
        return this.view().stream().filter(it -> (player.has(it) || it.price() == 0)).collect(Collectors.toList());
    }

    private void updateShop(Player player, Inventory inventory) {
        inventory.clear();

        int i = 0;
        for (Cosmetic c : this.cosmetics.values()) {
            ItemStack item = c.icon();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            // TODO solve this static ref
            LiteCosmetics liteCosmetics = LiteCosmetics.get();
            if (!liteCosmetics.player(player).has(c)) {
                lore.add(lore.size() - 2, ChatColor.RESET + "" + ChatColor.DARK_GRAY + " > " + ChatColor.WHITE +  "Price: " + ChatColor.GREEN + c.price());
            } else if (liteCosmetics.player(player).getSelected(this) != c){
                lore.add(lore.size() - 2, ChatColor.RESET + "" + ChatColor.DARK_GRAY + " > " + ChatColor.YELLOW + "Click here to equip this!");
            } else {
                lore.add(lore.size() - 2, ChatColor.RESET + "" + ChatColor.DARK_GRAY + " > " + ChatColor.RED + "Accessory equipped");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.setItem(i, item);
            i++;
        }
    }

    public Inventory getShop(Player player) {
        InventoryBuilder builder = new InventoryBuilder(ChatColor.RESET + "" + ChatColor.WHITE + ChatColor.BOLD + " --> " +  this.display, 5);
        int i = 0;
        builder.setInteraction((event) -> {
           ItemStack stack = event.getCurrentItem();
           ItemMeta meta = stack.getItemMeta();
           if (meta == null) return;
           List<String> lore = meta.getLore();
           if (lore == null) return;
           if (lore.isEmpty()) return;
           String id = ChatColor.stripColor(lore.get(lore.size() - 1));

           T cosmetic = this.get(id);
           if (cosmetic == null) return;

           LiteCoin liteCoin = LiteCosmeticsPlugin.getLiteCoin();
           LiteCosmetics liteCosmetics = LiteCosmetics.get();
           if (liteCoin.getBalance(player) >= cosmetic.price() && !liteCosmetics.player(player).has(cosmetic)) {
               liteCoin.setBalance(player, liteCoin.getBalance(player) - cosmetic.price());
               player.sendMessage(ChatColor.GREEN + "You've just bought " + ChatColor.RESET + cosmetic.displayName());
               liteCosmetics.player(player).give(cosmetic);
               updateShop(player, event.getInventory());
               return;
           }

           if (liteCosmetics.player(player).has(cosmetic) && liteCosmetics.player(player).getSelected(this) != cosmetic) {
               player.sendMessage(ChatColor.YELLOW + "You've just equipped the " + ChatColor.RESET + cosmetic.displayName());
               liteCosmetics.player(player).select(this, cosmetic);
               updateShop(player, event.getInventory());
               return;
           }
        });
        Inventory inventory = builder.build(player);
        updateShop(player, inventory);
        return inventory;
    }
}
