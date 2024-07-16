package dev.etery.litecosmetics.command;

import dev.etery.litecosmetics.LiteCosmetics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestTauntCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            LiteCosmetics cosmetics = LiteCosmetics.get();
            Player p = (Player) commandSender;
            p.getInventory().addItem(cosmetics.createTauntDiamond());
        }
        return true;
    }
}
