package dev.etery.litecosmetics.command;

import dev.etery.litecosmetics.LiteCosmetics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CosmeticShopCommand implements CommandExecutor {
    private final LiteCosmetics cosmetics;

    public CosmeticShopCommand(LiteCosmetics cosmetics) {
        this.cosmetics = cosmetics;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            cosmetics.openShop(null, p);
        }
        return true;
    }
}
