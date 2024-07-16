package dev.etery.litecosmetics;

import dev.etery.litecosmetics.command.CosmeticShopCommand;
import dev.etery.litecosmetics.command.TestTauntCommand;
import dev.etery.litecosmetics.cosmetic.Hat;
import dev.etery.litecosmetics.cosmetic.Taunt;
import dev.etery.litecosmetics.data.CategoryLoader;
import dev.etery.litecosmetics.impl.CosmeticPlayerSerializer;
import dev.etery.litecosmetics.event.PlayerListener;
import dev.etery.litecosmetics.impl.LiteCosmeticsImpl;
import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.plugin.java.JavaPlugin;

public final class LiteCosmeticsPlugin extends JavaPlugin {
    // TODO fix this lmao
    private static LiteCoin LITECOIN;
    public static LiteCoin getLiteCoin() {
        return LITECOIN;
    }
    private final LiteCosmetics cosmetics = LiteCosmetics.get();
    private final CategoryLoader<Taunt> tauntLoader = new CategoryLoader<>(this, "taunts", Taunt::from);
    private final CategoryLoader<Hat> hatLoader = new CategoryLoader<>(this, "hats", Hat::from);

    @Override
    public void onEnable() {

        // Plugin startup login
        LITECOIN = JavaPlugin.getPlugin(me.stephenminer.litecoin.LiteCoin.class);
        tauntLoader.load(cosmetics);
        hatLoader.load(cosmetics);
        ((LiteCosmeticsImpl) cosmetics).load();
        Category<Taunt> taunt = cosmetics.category("taunts");
        getCommand("taunt").setExecutor(new TestTauntCommand());
        getCommand("shop").setExecutor(new CosmeticShopCommand(cosmetics));
        getServer().getPluginManager().registerEvents(new PlayerListener(cosmetics), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ((LiteCosmeticsImpl) cosmetics).stop();
    }
}
