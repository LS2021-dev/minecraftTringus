package de.ferargent.minecraftTringus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnProtectionListener extends BukkitRunnable implements Listener {

    private final Plugin plugin;
    private final int spawnRadius;

    private final boolean spawnProtectionEnabled;
    private final World world = Bukkit.getWorld("world");

    private SpawnProtectionListener(Plugin plugin, int spawnRadius, boolean spawnProtectionEnabled) {
        this.plugin = plugin;
        this.spawnRadius = spawnRadius;
        this.spawnProtectionEnabled = spawnProtectionEnabled;

        this.runTaskTimer(this.plugin, 0, 3);
    }

    public static SpawnProtectionListener create(Plugin plugin) {
        var config = plugin.getConfig();
        if (!config.contains("spawnRadius") || !config.contains("spawnProtectionEnabled")) {
            plugin.saveResource("config.yml", true);
            plugin.reloadConfig();
        }
        return new SpawnProtectionListener(plugin, config.getInt("spawnRadius"), config.getBoolean("spawnProtectionEnabled"));
    }

    @Override
    public void run() {
        if (spawnProtectionEnabled) {
            world.getPlayers().forEach(player -> {
                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.isOp()) return;
                if (player.getLocation().distance(world.getSpawnLocation()) <= spawnRadius) {
                    player.setGameMode(GameMode.ADVENTURE);
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                }
            });
        }
    }
}
