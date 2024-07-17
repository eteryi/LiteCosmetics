package dev.etery.litecosmetics;

import dev.etery.litecosmetics.command.CosmeticShopCommand;
import dev.etery.litecosmetics.cosmetic.Hat;
import dev.etery.litecosmetics.cosmetic.Taunt;
import dev.etery.litecosmetics.data.CategoryLoader;
import dev.etery.litecosmetics.listener.PlayerListener;
import dev.etery.litecosmetics.impl.LiteCosmeticsImpl;
import me.stephenminer.litecoin.LiteCoin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class LiteCosmeticsPlugin extends JavaPlugin {
    // TODO fix this lmao
    // TODO allow for stained glass usage
    // TODO Create LiteCosmeticsRegisterEvent and LiteCosmeticsSelectEvent
    // TODO implement LiteCoin better
    private static LiteCoin LITECOIN;
    public static LiteCoin getLiteCoin() {
        return LITECOIN;
    }
    private final LiteCosmetics cosmetics = LiteCosmetics.get();
    private final CategoryLoader<Taunt> tauntLoader = new CategoryLoader<>(this, "taunts", Taunt::from, () -> {
        File tauntDir = new File(this.getDataFolder(), "taunts");
        if (!tauntDir.exists()) {
            if (!tauntDir.mkdir()) {
                throw new RuntimeException("Couldn't create /taunts");
            }
            this.saveResource("taunts/cross.png", false);
            this.saveResource("taunts/sword.png", false);
        }
    });
    private final CategoryLoader<Hat> hatLoader = new CategoryLoader<>(this, "hats", Hat::from);

    @Override
    public void onEnable() {
        // Plugin startup login
        LITECOIN = JavaPlugin.getPlugin(me.stephenminer.litecoin.LiteCoin.class);
        hatLoader.load(cosmetics);
        tauntLoader.load(cosmetics);
        ((LiteCosmeticsImpl) cosmetics).load();
        getCommand("shop").setExecutor(new CosmeticShopCommand(cosmetics));
        getServer().getPluginManager().registerEvents(new PlayerListener(cosmetics), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ((LiteCosmeticsImpl) cosmetics).stop();
    }
}
