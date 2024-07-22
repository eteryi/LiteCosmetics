package dev.etery.litecosmetics.cosmetic;

import com.avaje.ebean.validation.NotNull;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KillMessage implements Cosmetic {
    // TODO create a JIT manager to handle all JIT's created by the Kill Messages
    // TODO make post process scripts optional (aka dont crash when the post isnt provided as a file)
    public static final LuaJit JIT = new LuaJit();
    static {
        JIT.openLibraries();
        JIT.pushJavaObject(System.out);
        JIT.setGlobal("sys");

        Map<String, String> colorMap = new HashMap<>();
        Arrays.stream(ChatColor.values()).forEach(it -> colorMap.put(it.name().toLowerCase(), it.toString()));

        JIT.push(colorMap);
        JIT.setGlobal("color");
    }

    private final String id;
    private final String name;
    private final int price;
    private final String description;
    private final ItemStack icon;
    private final Map<String, String> formats;
    private final RefLuaValue postFunc;
    // litebridge.death.void

    public KillMessage(String id, String name, int price, String description, ItemStack icon, String postScript, Map<String, String> format) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.icon = icon;
        this.formats = format;

        // TODO create a general JIT instead of one per death message
        JIT.run(postScript);
        RefLuaValue object = (RefLuaValue) JIT.get(id.split(":", 2)[1] + "_post");
        this.postFunc = object;
        System.out.println(object.call("hello")[0].toJavaObject());
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

        Map<String, String> formatting = new HashMap<>();

        MemorySection formatSection = (MemorySection) section.get("format");
        Map<String, Object> formats = formatSection.getValues(false);

        formats.forEach((k, v) -> {
            if (v instanceof String) {
                formatting.put(k, (String) v);
            }
        });

        File messageDir = new File(JavaPlugin.getPlugin(LiteCosmeticsPlugin.class).getDataFolder(), "kill_messages");
        File postScript = new File(messageDir, id.split(":", 2)[1] + ".lua");

        String rawScript;
        try {
            rawScript = new String(Files.readAllBytes(postScript.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new KillMessage(id, name, price, description, stack, rawScript, formatting);
    }

    public @NotNull String translate(String key, String... template) {
        String format = this.formats.get(key);
        if (format == null) return key;


        String rawString = String.format(format, template);
        return ChatColor.translateAlternateColorCodes('&', (String) Objects.requireNonNull(this.postFunc.call(rawString)[0].toJavaObject()));
    }
}
