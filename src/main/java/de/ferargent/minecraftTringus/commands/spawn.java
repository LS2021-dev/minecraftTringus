package de.ferargent.minecraftTringus.commands;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class spawn implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;
        Location location = player.getWorld().getSpawnLocation();
        location.setY(location.getY() + 0.5);
        location.setDirection(player.getLocation().getDirection());
        player.teleport(location);
        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 60, 0.2, 0.5, 0.2, 0.1, null, true);
        player.sendMessage("§bTeleported to spawn!");
        return false;
    }
}
