package de.ferargent.minecraftTringus.commands;

import de.ferargent.minecraftTringus.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class setWarp implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String xStr = args[0];

        Player player = (Player) commandSender;

        double x, y, z;

        if (xStr.equals("target")) {
            x = player.getTargetBlock(null, 100).getX();
            y = player.getTargetBlock(null, 100).getY();
            z = player.getTargetBlock(null, 100).getZ();
        } else {
            String yStr = args[1];
            String zStr = args[2];
            if (xStr.equals("~")) {
                x = player.getLocation().getX();
            } else {
                x = parseCoordinate(xStr, player.getLocation().getX(), player);
            }

            if (yStr.equals("~")) {
                y = player.getLocation().getY();
            } else {
                y = parseCoordinate(yStr, player.getLocation().getY(), player);
            }

            if (zStr.equals("~")) {
                z = player.getLocation().getZ();
            } else {
                z = parseCoordinate(zStr, player.getLocation().getZ(), player);
            }
        }


        commandSender.sendMessage("§bWarp set to " + x + " " + y + " " + z + "!");
        PersistentDataContainer data = commandSender.getServer().getWorld("world").getPersistentDataContainer();
        data.set(new NamespacedKey(Main.getPlugin(), "warpPosition"), PersistentDataType.STRING, x + " " + y + " " + z);
        return false;
    }

    private double parseCoordinate(String coordStr, double defaultValue, Player player) {
        if (coordStr.startsWith("~")) {
            if (coordStr.length() > 1) {
                String relativeValue = coordStr.substring(1);
                try {
                    double offset = Double.parseDouble(relativeValue);
                    return defaultValue + offset;
                } catch (NumberFormatException e) {
                    // Handle invalid relative value
                    player.sendMessage("§cInvalid coordinate value!");
                }
            } else {
                return defaultValue;
            }
        } else {
            try {
                return Double.parseDouble(coordStr);
            } catch (NumberFormatException e) {
                // Handle invalid coordinate value
                player.sendMessage("§cInvalid coordinate value!");
            }
        }
        return defaultValue;
    }
}
