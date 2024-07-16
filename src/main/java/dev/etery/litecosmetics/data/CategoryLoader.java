package dev.etery.litecosmetics.data;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.LiteCosmetics;
import dev.etery.litecosmetics.cosmetic.Cosmetic;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

public class CategoryLoader<T extends Cosmetic> {
    private final File configFile;
    private final YamlConfiguration configuration;
    private Category<T> category;
    private final String id;
    private final BiFunction<String, MemorySection, T> loader;
    private final Runnable loadCallback;

    public CategoryLoader(JavaPlugin plugin, String name, BiFunction<String, MemorySection, T> loader, Runnable onLoad) {
        this.configFile = new File(plugin.getDataFolder(), name + ".yml");
        if (!this.configFile.exists()) {
            plugin.saveResource(name + ".yml", false);
        }
        this.id = name;
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
        this.loader = loader;
        this.loadCallback = onLoad;
    }

    public CategoryLoader(JavaPlugin plugin, String name, BiFunction<String, MemorySection, T> loader) {
        this(plugin, name, loader, () -> {});
    }

    public void load(LiteCosmetics cosmetics) {
        this.loadCallback.run();
        String name = configuration.getString("name");
        String description = configuration.getString("description");
        String materialStr = configuration.getString("icon");
        Material material;
        try {
            material = Material.valueOf(materialStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            material = Material.PAPER;
        }

        this.category = new Category<>(this.id, name, description, new ItemStack(material));

        MemorySection section = (MemorySection) configuration.get("cosmetics");
        Map<String, Object> map = section.getValues(false);
        map.forEach((k, v) -> {
            T cosmetic = loader.apply(this.id + ":" + k, (MemorySection) v);
            cosmetics.logger().info(cosmetic.id() + " was loaded");
            this.category.add(cosmetic);
        });

        cosmetics.registerCategory(category);
    }

    public Category<T> category() {
        return this.category;
    }
}