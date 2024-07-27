package dev.etery.litecosmetics.cosmetic;

import com.avaje.ebean.validation.NotNull;
import dev.etery.litecosmetics.JitManager;
import dev.etery.litecosmetics.LiteCosmeticsPlugin;
import dev.etery.litecosmetics.data.ItemDecoder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import party.iroiro.luajava.luajit.LuaJit;
import party.iroiro.luajava.value.RefLuaValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class KillMessage implements Cosmetic {

    private static class PostScript {
        private static final HashMap<String, PostScript> cache = new HashMap<>();

        public static PostScript load(String name) {
            if (cache.get(name) != null) return cache.get(name);
            PostScript script = new PostScript(name);
            script.load();
            cache.put(name, script);
            return script;
        }

        private final String id;
        private RefLuaValue postFunc;

        private PostScript(String id) {
            this.id = id;
        }

        private void load() {
            File messageDir = new File(JavaPlugin.getPlugin(LiteCosmeticsPlugin.class).getDataFolder(), "kill_messages");
            File postScript = new File(messageDir, id + ".lua");
            String rawScript;
            try {
                rawScript = new String(Files.readAllBytes(postScript.toPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            LuaJit jit = JitManager.create();
            jit.run(rawScript);
            RefLuaValue object = (RefLuaValue) jit.get("post");
            this.postFunc = object;
        }

        public String apply(String text) {
            return (String) this.postFunc.call(text)[0].toJavaObject();
        }
    }

    private static class NullScript extends PostScript {
        public NullScript() {
            super("normal");
        }

        @Override
        public String apply(String text) {
            return text;
        }
    }

    private static final PostScript NULL_SCRIPT = new NullScript();

    private final String id;
    private final String name;
    private final int price;
    private final String description;
    private final ItemStack icon;
    private final Map<String, String> formats;
    private final PostScript postScript;
    // litebridge.death.void

    public KillMessage(String id, String name, int price, String description, ItemStack icon, String postId, Map<String, String> format) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.icon = icon;
        this.formats = format;
        this.postScript = postId.isEmpty() ? NULL_SCRIPT : PostScript.load(postId);
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String displayName() {
        return this.name;
    }

    @Override
    public int price() {
        return this.price;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public ItemStack rawIcon() {
        return this.icon.clone();
    }

    public static KillMessage from(String id, MemorySection section) {
        String name = section.getString("display");
        String description = section.getString("description");
        ItemStack stack = ItemDecoder.decode(section, "icon");
        int price = section.getInt("price");
        String postEffect = section.getString("post", "");

        Map<String, String> formatting = new HashMap<>();

        MemorySection formatSection = (MemorySection) section.get("format");
        Map<String, Object> formats = formatSection.getValues(false);

        formats.forEach((k, v) -> {
            if (v instanceof String) {
                formatting.put(k, (String) v);
            }
        });

        return new KillMessage(id, name, price, description, stack, postEffect, formatting);
    }

    public @NotNull String translate(String key, String... template) {
        String format = this.formats.get(key);
        if (format == null) return key;


        String rawString = String.format(format, template);
        return ChatColor.translateAlternateColorCodes('&', this.postScript.apply(rawString));
    }
}
