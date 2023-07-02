package de.ferargent.minecraftTringus.commands;

import de.ferargent.minecraftTringus.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class sealSpawn implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        boolean enableSpawn = Boolean.parseBoolean(args[0]);
        if (enableSpawn) {
            commandSender.sendMessage("§bSpawn is now sealed!");
        } else {
            commandSender.sendMessage("§cSpawn is now unsealed!");
        }
        PersistentDataContainer data = commandSender.getServer().getWorld("world").getPersistentDataContainer();
        data.set(new NamespacedKey(Main.getPlugin(), "sealSpawn"), PersistentDataType.BOOLEAN, enableSpawn);
        return false;
    }
}
