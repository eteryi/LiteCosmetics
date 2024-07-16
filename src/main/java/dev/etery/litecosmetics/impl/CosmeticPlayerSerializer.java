package dev.etery.litecosmetics.impl;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.LiteCosmetics;
import dev.etery.litecosmetics.LiteCosmeticsPlugin;
import dev.etery.litecosmetics.cosmetic.Cosmetic;
import dev.etery.litecosmetics.data.CosmeticPlayer;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CosmeticPlayerSerializer {
    private final LiteCosmeticsImpl cosmetics;
    private File playerDataFile;
    private YamlConfiguration playerData;

    protected CosmeticPlayerSerializer(LiteCosmeticsImpl cosmetics) {
        this.cosmetics = cosmetics;
    }

    protected void load() {
        // TODO fix this LMAO
        LiteCosmeticsPlugin plugin = JavaPlugin.getPlugin(LiteCosmeticsPlugin.class);
        this.playerDataFile = new File(plugin.getDataFolder(), "players.yml");
        if (!this.playerDataFile.exists()) {
            plugin.saveResource( "players.yml", false);
        }
        this.playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        Map<String, Object> map = this.playerData.getValues(false);
        map.forEach((k, v) -> {
            UUID uuid = UUID.fromString(k);
            MemorySection section = (MemorySection) v;
            List<String> cosmeticsBought = section.getStringList("cosmetics");
            CosmeticPlayer player = new CosmeticPlayerImpl();
            cosmeticsBought.forEach(player::give);
            MemorySection selected = (MemorySection) section.get("selected");
            if (selected != null) {
                selected.getValues(false).forEach((category, cosmetic) -> {
                    Category<?> c = cosmetics.category(category);
                    if (c == null) return;
                    Cosmetic cosm = c.get((String) cosmetic);
                    if (cosm == null) return;
                    player.select(cosmetics.category(category), c.get((String) cosmetic));
                });
            }
            cosmetics.addPlayer(uuid, player);
        });
    }

    protected void save() {
        cosmetics.players().forEach(entry -> {
            playerData.set(entry.getKey().toString() + ".cosmetics", entry.getValue().boughtCosmetics());
            entry.getValue().selectedCosmetics().forEach(category -> {
                playerData.set(entry.getKey().toString() + ".selected." + category.getKey().id, category.getValue().id());
            });
        });
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
