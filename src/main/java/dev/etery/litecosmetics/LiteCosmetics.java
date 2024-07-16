package dev.etery.litecosmetics;

import dev.etery.litecosmetics.cosmetic.Cosmetic;
import dev.etery.litecosmetics.data.CosmeticPlayer;
import dev.etery.litecosmetics.impl.LiteCosmeticsImpl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.logging.Logger;

public interface LiteCosmetics {
    static LiteCosmetics get() {
        return LiteCosmeticsImpl.impl();
    }

    <T extends Cosmetic> void registerCategory(Category<T> category);
    Collection<Category<?>> categories();
    <T extends Cosmetic> Category<T> category(String id);

    CosmeticPlayer player(Player player);
    Logger logger();

    void openShop(Player player);

    ItemStack createTauntDiamond();
}
