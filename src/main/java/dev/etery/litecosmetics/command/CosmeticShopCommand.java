package dev.etery.litecosmetics.command;

import dev.etery.litecosmetics.LiteCosmetics;
import dev.etery.litecosmetics.inventory.InventoryBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

public class CosmeticShopCommand implements CommandExecutor {
    private final LiteCosmetics cosmetics;

    public CosmeticShopCommand(LiteCosmetics cosmetics) {
        this.cosmetics = cosmetics;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            cosmetics.openShop(p);
        }
        return true;
    }
}
