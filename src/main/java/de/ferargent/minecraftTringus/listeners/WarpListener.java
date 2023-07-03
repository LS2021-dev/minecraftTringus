package de.ferargent.minecraftTringus.listeners;

import de.ferargent.minecraftTringus.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WarpListener extends BukkitRunnable implements Listener {
    private final Plugin plugin;


    private final boolean warpProtectionEnabled;
    private final int warpProtectionRadius;

    private final int warpTPRadius;

    private final World world = Bukkit.getWorld("world");

    private final List<Player> hasEffect = new ArrayList<>();

    private WarpListener(Plugin plugin, boolean warpProtectionEnabled, int warpProtectionRadius, int warpTPRadius) {
        this.plugin = plugin;
        this.warpProtectionEnabled = warpProtectionEnabled;
        this.warpProtectionRadius = warpProtectionRadius;
        this.warpTPRadius = warpTPRadius;
        this.runTaskTimer(this.plugin, 0, 3);
    }

    public static WarpListener create(Plugin plugin) {
        var config = plugin.getConfig();
        if (!config.contains("spawnRadius")) {
            plugin.saveResource("config.yml", true);
            plugin.reloadConfig();
        }
        return new WarpListener(plugin, config.getBoolean("warpProtectionEnabled"), config.getInt("warpProtectionRadius"), config.getInt("warpTPRadius"));
    }

    @Override
    public void run() {
        PersistentDataContainer data = world.getPersistentDataContainer();
        String warpPositionStr = data.get(new NamespacedKey(Main.getPlugin(), "warpPosition"), PersistentDataType.STRING);
        if (!(warpPositionStr == null || warpPositionStr.equals(""))) {
            String[] warpPositionSplit = warpPositionStr.split(" ");
            double warpX = Double.parseDouble(warpPositionSplit[0]);
            double warpY = Double.parseDouble(warpPositionSplit[1]);
            double warpZ = Double.parseDouble(warpPositionSplit[2]);
            Location warpPostion = new Location(world, warpX, warpY, warpZ);
            world.getPlayers().forEach(player -> {
                if (player.getLocation().distance(warpPostion) < warpTPRadius && !hasEffect.contains(player)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 3));
                    hasEffect.add(player);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.removeScoreboardTag("warp");
                        Location location = player.getWorld().getSpawnLocation();
                        location.setY(location.getY() + 0.5);
                        location.setDirection(player.getLocation().getDirection());
                        player.teleport(location);
                        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 60, 0.2, 0.5, 0.2, 0.1, null, true);
                        player.sendMessage("§bTeleported to spawn!");
                        hasEffect.remove(player);
                    }, 50);
                }
                if (warpProtectionEnabled) {
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
                    if (player.getLocation().distance(warpPostion) < warpProtectionRadius) {
                        player.setGameMode(GameMode.ADVENTURE);
                        player.addScoreboardTag("warp");
                    } else if (player.getScoreboardTags().contains("warp") && !player.getScoreboardTags().contains("spawn")) {
                        player.setGameMode(GameMode.SURVIVAL);
                        player.removeScoreboardTag("warp");
                    }
                }
            });
        }
    }
}
