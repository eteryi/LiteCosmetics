package dev.etery.litecosmetics.impl;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.LiteCosmetics;
import dev.etery.litecosmetics.cosmetic.Cosmetic;
import dev.etery.litecosmetics.cosmetic.Hat;
import dev.etery.litecosmetics.cosmetic.Taunt;
import dev.etery.litecosmetics.data.CosmeticPlayer;
import dev.etery.litecosmetics.event.LCOpenShopEvent;
import dev.etery.litecosmetics.inventory.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        if (cosmeticPlayers.get(player.getUniqueId()) == null) cosmeticPlayers.put(player.getUniqueId(), new CosmeticPlayerImpl(player.getUniqueId()));
        return cosmeticPlayers.get(player.getUniqueId());
    }

    @Override
    public Logger logger() {
        return logger;
    }

    private static ItemStack getItemWithName(ItemStack itemStack, String displayName, String... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + displayName);
        meta.setLore(Arrays.stream(lore).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void openShop(Inventory previousInventory, Player player) {
        LCOpenShopEvent lcEvent = new LCOpenShopEvent(player, this, null);
        Bukkit.getPluginManager().callEvent(lcEvent);
        if (lcEvent.isCancelled()) return;

        InventoryBuilder inventory = new InventoryBuilder(ChatColor.RESET + "" + ChatColor.GOLD + ChatColor.BOLD + "Shop", 3);
        Category<Hat> hats = category("hats");
        Category<Taunt> taunts = category("taunts");

        inventory.setItem(9, taunts.icon());
        inventory.setItem(11, hats.icon());

        inventory.setItem(18, getItemWithName(new ItemStack(Material.FEATHER), ChatColor.GRAY + "Back to last menu"));
        inventory.setItem(26, getItemWithName(new ItemStack(Material.BARRIER), ChatColor.RED + "Close Menu"));

        short[] backgroundColor = {11, 0, 14};

        for (int i = 0; i < 27; i++) {
            ItemStack background = getItemWithName(new ItemStack(Material.STAINED_GLASS_PANE, 1, backgroundColor[i / 9]), ChatColor.DARK_AQUA + " ");
            if (inventory.getItem(i) == null) inventory.setItem(i, background);
        }

        inventory.setInteraction((event) -> {
            if (event.getSlot() == 18) {
                event.getWhoClicked().closeInventory();
                if (previousInventory != null) event.getWhoClicked().openInventory(previousInventory);
                return;
            }
            if (event.getSlot() == 26) {
                event.getWhoClicked().closeInventory();
                return;
            }
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
            Inventory categoryShop = category.getShop((Player) event.getWhoClicked(), event.getClickedInventory());
            if (categoryShop != null) event.getWhoClicked().openInventory(categoryShop);
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
