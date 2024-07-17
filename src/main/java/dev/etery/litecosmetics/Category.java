package dev.etery.litecosmetics;

import dev.etery.litecosmetics.cosmetic.Cosmetic;
import dev.etery.litecosmetics.data.CosmeticPlayer;
import dev.etery.litecosmetics.event.LCOpenShopEvent;
import dev.etery.litecosmetics.inventory.InventoryBuilder;
import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Category<T extends Cosmetic> {
    private final HashMap<String, T> cosmetics;
    private final ArrayList<T> cosmeticView;
    public final String id;
    private final String display;
    private final String description;
    private final ItemStack icon;

    public Category(String id, String displayName, String description, ItemStack icon) {
        this.id = id;
        this.display = displayName;
        this.cosmeticView = new ArrayList<>();
        this.icon = icon;
        this.description = description;
        this.cosmetics = new HashMap<>();
    }

    public void add(T cosmetic) {
        this.cosmetics.put(cosmetic.id(), cosmetic);
        this.cosmeticView.add(cosmetic);
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

    private static ItemStack getItemWithName(ItemStack itemStack, String displayName, String... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + displayName);
        meta.setLore(Arrays.stream(lore).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void updateShop(Player player, Inventory inventory, int page) {
        inventory.clear();

        List<T> cosmetics = this.cosmeticView;
        for (int i = 0; i < cosmetics.size(); i++) {
            if (i >= (9 * 3)) break;
            int ind = i + (page * (9 * 3));
            if (ind >= cosmeticView.size()) break;
            Cosmetic c = cosmetics.get(ind);
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
        }

        inventory.setItem(9 * 3, getItemWithName(new ItemStack(Material.ARROW), ChatColor.GRAY + "Last Page"));
        inventory.setItem((9 * 3) + 3, getItemWithName(new ItemStack(Material.FEATHER), ChatColor.GRAY + "Go back to the last menu"));
        inventory.setItem((9 * 3) + 4, getItemWithName(new ItemStack(Material.BARRIER), ChatColor.RED + "Close menu"));
        inventory.setItem((9 * 3) + 5, getItemWithName(new ItemStack(Material.EMERALD), ChatColor.GOLD + "Litecoins: " + ChatColor.GREEN +
                LiteCosmeticsPlugin.getLiteCoin().getBalance(player)));
        inventory.setItem((9 * 3) + 8, getItemWithName(new ItemStack(Material.ARROW), ChatColor.GRAY + "Next Page"));
        for (int j = 0; j < 9; j++) {
            int index = j + (9 * 3);
            if (inventory.getItem(index) != null) continue;
            inventory.setItem(j + (9 * 3), getItemWithName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0), ChatColor.GRAY + " "));
        }
    }

    public Inventory getShop(Player player, Inventory previous) {
        LCOpenShopEvent lcEvent = new LCOpenShopEvent(player, LiteCosmetics.get(), this);
        Bukkit.getPluginManager().callEvent(lcEvent);
        if (lcEvent.isCancelled()) return null;
        final int[] currentPage = {0};
        InventoryBuilder builder = new InventoryBuilder(ChatColor.RESET + "" + ChatColor.WHITE + ChatColor.BOLD + " --> " +  this.display, 4);
        builder.setInteraction((event) -> {
            int slot = event.getSlot();
            ItemStack stack = event.getCurrentItem();
            if (slot == 9 * 3) {
                currentPage[0] = Math.max(0, currentPage[0] - 1);
                updateShop(player, event.getInventory(), currentPage[0]);
                return;
            }
            if (slot == (9 * 3) + 3) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().openInventory(previous);
                return;
            }
            if (slot == (9 * 3) + 4) {
                event.getWhoClicked().closeInventory();
                return;
            }
            if (slot == (9 * 3) + 8) {
                currentPage[0] = Math.min(this.cosmeticView.size() / 27, currentPage[0] + 1);
                updateShop(player, event.getInventory(), currentPage[0]);
                return;
            }
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
                liteCoin.incrementBalance(player, -cosmetic.price());
                player.sendMessage(ChatColor.GREEN + "You've just bought " + ChatColor.RESET + cosmetic.displayName());
                liteCosmetics.player(player).give(cosmetic);
                updateShop(player, event.getInventory(), currentPage[0]);
                return;
            }

            if (liteCosmetics.player(player).has(cosmetic) && liteCosmetics.player(player).getSelected(this) != cosmetic) {
                player.sendMessage(ChatColor.YELLOW + "You've just equipped the " + ChatColor.RESET + cosmetic.displayName());
                liteCosmetics.player(player).select(this, cosmetic);
                updateShop(player, event.getInventory(), currentPage[0]);
            }
        });
        Inventory inventory = builder.build(player);
        updateShop(player, inventory, currentPage[0]);
        return inventory;
    }
}