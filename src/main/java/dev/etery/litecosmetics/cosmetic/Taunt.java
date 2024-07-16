package dev.etery.litecosmetics.cosmetic;

import dev.etery.litecosmetics.LiteCosmeticsPlugin;
import org.bukkit.*;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Locale;

public class Taunt implements Cosmetic {
    public final int price;
    public final String name;
    public final String id;
    public final String description;
    public final ItemStack icon;
    public final BufferedImage tauntImage;
    public final Sound tauntSound;
    public final float volume;
    public final float pitch;
    private final int[] rgb;

    public Taunt(String id, String tauntName, String description, BufferedImage image, Sound sound, int price, float volume, float pitch, Material material) {
        this.tauntImage = image;
        this.name = tauntName;
        this.id = id;
        this.description = description;
        this.price = price;
        this.tauntSound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.icon = new ItemStack(material);
        rgb = this.tauntImage.getRGB(0, 0, this.tauntImage.getWidth(), this.tauntImage.getHeight(), null, 0, this.tauntImage.getWidth());
    }

    public void use(Player player) {
        player.getWorld().playSound(player.getLocation(), this.tauntSound, 1f, 1f);

        Vector dir = player.getLocation().getDirection();
        dir.setY(0);
        dir.normalize();
        dir = dir.crossProduct(new Vector(0, 1, 0));
        dir.multiply(0.25);

        Vector copyDir = new Vector();
        copyDir.copy(dir);
        copyDir.multiply(-((double)this.tauntImage.getWidth()) / 2.0);

        Location startLoc = player.getLocation().add(0, 2, 0).add(copyDir);
        for (int y = 1; y <= this.tauntImage.getHeight(); y++) {
            for (int x = 0; x < this.tauntImage.getWidth(); x++) {
                int index = (this.tauntImage.getHeight() - y) * this.tauntImage.getWidth() + x;
                int A = (rgb[index] >> 24) & 0xff;
                int R = (rgb[index] >> 16) & 0xff;
                int G = (rgb[index] >> 8) & 0xff;
                int B = rgb[index] & 0xff;

                if (A == 255) {
                    startLoc.getWorld().spigot().playEffect(startLoc, Effect.COLOURED_DUST, 0, 1, ((float)R) / 255F, ((float)G) / 255F, ((float) B) / 255F, 1, 0, 64);
                }

                startLoc = startLoc.add(dir);
            }
            copyDir = new Vector();
            copyDir.copy(dir);
            copyDir.multiply(-((double) this.tauntImage.getWidth()));
            startLoc = startLoc.add(copyDir);
            startLoc = startLoc.add(0, 0.25, 0);
        }
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String displayName() {
        return ChatColor.translateAlternateColorCodes('&', this.name);
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

    public static Taunt from(String id, MemorySection section) {
        String name = section.getString("display");
        String rawSound = section.getString("sound");
        String description = section.getString("description");
        float vol = (float) section.getDouble("vol");
        float pitch = (float) section.getDouble("pitch");
        String rawIcon = section.getString("icon");
        int price = section.getInt("price");

        File tauntDir = new File(JavaPlugin.getPlugin(LiteCosmeticsPlugin.class).getDataFolder(), "taunts");
        if (!tauntDir.exists()) if (!tauntDir.mkdir()) throw new RuntimeException("Couldn't find a /taunts directory for the images");
        File imageFile = new File(tauntDir, id.split(":", 2)[1] + ".png");
        System.out.println(imageFile.getAbsolutePath());
        System.out.println(imageFile.exists());
        BufferedImage image;
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Sound sound;
        Material material;
        sound = Sound.valueOf(rawSound.toUpperCase(Locale.ROOT));
        material = Material.valueOf(rawIcon.toUpperCase(Locale.ROOT));
        return new Taunt(id, name, description, image, sound, price, vol, pitch, material);
    }
}
