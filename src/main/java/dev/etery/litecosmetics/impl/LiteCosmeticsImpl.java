package dev.etery.litecosmetics.impl;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.LiteCosmetics;
import dev.etery.litecosmetics.cosmetic.Cosmetic;
import dev.etery.litecosmetics.data.CosmeticPlayer;
import dev.etery.litecosmetics.inventory.InventoryBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Logger;

public class LiteCosmeticsImpl implements LiteCosmetics {
    private final Map<String, Category<?>> categories = new HashMap<>();
    private static final LiteCosmeticsImpl IMPL = new LiteCosmeticsImpl();
    private final HashMap<UUID, CosmeticPlayer> cosmeticPlayers = new HashMap<>();
    private final Logger logger = Logger.getLogger("LiteCosmetics");
    private final CosmeticPlayerSerializer serializer = new CosmeticPlayerSerializer(this);

    public static LiteCosmetics impl() {
        return IMPL;
    }

    public void load() {
        this.serializer.load();
    }

    public void stop() {
        this.serializer.save();
    }

    @Override
    public <T extends Cosmetic> void registerCategory(Category<T> category) {
        categories.put(category.id, category);
    }

    @Override
    public Collection<Category<?>> categories() {
        return categories.values();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Cosmetic> Category<T> category(String id) {
        return (Category<T>) categories.get(id);
    }

    @Override
    public CosmeticPlayer player(Player player) {
        if (cosmeticPlayers.get(player.getUniqueId()) == null) cosmeticPlayers.put(player.getUniqueId(), new CosmeticPlayerImpl());
        return cosmeticPlayers.get(player.getUniqueId());
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public void openShop(Player player) {
        InventoryBuilder inventory = new InventoryBuilder(ChatColor.RESET + "" + ChatColor.GOLD + ChatColor.BOLD + "Shop", 5);
        int i = 0;
        for (Category<?> category : categories()) {
            ItemStack item = category.icon();
            inventory.setItem(10 + (i * 3), item);
            i++;
        }

        inventory.setInteraction((event) -> {
           ItemStack stack = event.getCurrentItem();
           ItemMeta meta = stack.getItemMeta();
           if (meta == null) return;
           List<String> lore = meta.getLore();
           if (lore == null) return;
           if (lore.isEmpty()) return;

           String id = ChatColor.stripColor(lore.get(lore.size() - 1));
           if (category(id) == null) return;
           Category<Cosmetic> category = category(id);
           event.getWhoClicked().closeInventory();
           event.getWhoClicked().openInventory(category.getShop((Player) event.getWhoClicked()));
        });
        Inventory inv = inventory.build(player);
        player.openInventory(inv);
    }

    @Override
    public ItemStack createTauntDiamond() {
        ItemStack diamond = new ItemStack(Material.DIAMOND);
        ItemMeta meta = diamond.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + "Taunts");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "Litebridge");
        meta.setLore(lore);
        diamond.setItemMeta(meta);
        return diamond;
    }

    public Set<Map.Entry<UUID, CosmeticPlayer>> players() {
        return this.cosmeticPlayers.entrySet();
    }

    public void addPlayer(UUID uuid, CosmeticPlayer player) {
        this.cosmeticPlayers.put(uuid, player);
    }

}
