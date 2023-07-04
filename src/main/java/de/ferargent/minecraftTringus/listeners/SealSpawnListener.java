package de.ferargent.minecraftTringus.listeners;

import de.ferargent.minecraftTringus.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;


public class SealSpawnListener extends BukkitRunnable implements Listener {
    private final Plugin plugin;

    private final int spawnRadius;

    private final World world = Bukkit.getWorld("world");

    private SealSpawnListener(Plugin plugin, int spawnRadius) {
        this.plugin = plugin;
        this.spawnRadius = spawnRadius;
        this.runTaskTimer(this.plugin, 0, 3);
    }

    public static SealSpawnListener create(Plugin plugin) {
        var config = plugin.getConfig();
        if (!config.contains("spawnRadius")) {
            plugin.saveResource("config.yml", true);
            plugin.reloadConfig();
        }
        return new SealSpawnListener(plugin, config.getInt("spawnRadius"));
    }

    @Override
    public void run() {
        PersistentDataContainer data = world.getPersistentDataContainer();
        if (data.get(new NamespacedKey(Main.getPlugin(), "sealSpawn"), PersistentDataType.BOOLEAN) == null) return;
        if (Boolean.TRUE.equals(data.get(new NamespacedKey(Main.getPlugin(), "sealSpawn"), PersistentDataType.BOOLEAN))) {
            world.getPlayers().forEach(player -> {
                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.isOp())
                    return;
                if (player.getLocation().distance(world.getSpawnLocation()) > spawnRadius) {
                    player.teleport(world.getSpawnLocation());
                }
            });
        }
    }

}
