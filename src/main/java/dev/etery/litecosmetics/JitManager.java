package dev.etery.litecosmetics;

import org.bukkit.ChatColor;
import party.iroiro.luajava.AbstractLua;
import party.iroiro.luajava.luajit.LuaJit;

import java.util.*;

public class JitManager {
    private static final Collection<LuaJit> INSTANCES = new ArrayList<>();

    public static LuaJit create() {
        LuaJit jit = new LuaJit();
        jit.openLibraries();

        jit.pushJavaObject(System.out);
        jit.setGlobal("sys");

        Map<String, String> colorMap = new HashMap<>();
        Arrays.stream(ChatColor.values()).forEach(it -> colorMap.put(it.name().toLowerCase(), it.toString()));

        jit.push(colorMap);
        jit.setGlobal("color");

        INSTANCES.add(jit);

        return jit;
    }

    protected static void clean() {
        INSTANCES.forEach(AbstractLua::close);
    }
}
