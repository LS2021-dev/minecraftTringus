package de.ferargent.minecraftTringus.listeners;

import de.ferargent.minecraftTringus.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WarpListener extends BukkitRunnable implements Listener {
    private final Plugin plugin;


    private final boolean warpProtectionEnabled;
    private final int warpProtectionRadius;

    private final int warpTPRadius;

    private final boolean warpGriefProtectionEnabled;

    private final int warpGriefProtectionRadius;

    private final World world = Bukkit.getWorld("world");

    private final List<Player> hasEffect = new ArrayList<>();

    private WarpListener(Plugin plugin, boolean warpProtectionEnabled, int warpProtectionRadius, int warpTPRadius, boolean warpGriefProtectionEnabled, int warpGriefProtectionRadius) {
        this.plugin = plugin;
        this.warpProtectionEnabled = warpProtectionEnabled;
        this.warpProtectionRadius = warpProtectionRadius;
        this.warpTPRadius = warpTPRadius;
        this.warpGriefProtectionEnabled = warpGriefProtectionEnabled;
        this.warpGriefProtectionRadius = warpGriefProtectionRadius;
        this.runTaskTimer(this.plugin, 0, 3);
    }

    public static WarpListener create(Plugin plugin) {
        var config = plugin.getConfig();
        if (!config.contains("warpProtectionEnabled") || !config.contains("warpProtectionRadius") || !config.contains("warpTPRadius") || !config.contains("warpGriefProtectionEnabled") || !config.contains("warpGriefProtectionRadius")) {
            plugin.saveResource("config.yml", true);
            plugin.reloadConfig();
        }
        return new WarpListener(plugin, config.getBoolean("warpProtectionEnabled"), config.getInt("warpProtectionRadius"), config.getInt("warpTPRadius"), config.getBoolean("warpGriefProtectionEnabled"), config.getInt("warpGriefProtectionRadius"));
    }

    private Location warpPosition() {
        PersistentDataContainer data = world.getPersistentDataContainer();
        String warpPositionStr = data.get(new NamespacedKey(Main.getPlugin(), "warpPosition"), PersistentDataType.STRING);
        if (warpPositionStr == null || warpPositionStr.equals("")) return null;
        String[] warpPositionSplit = warpPositionStr.split(" ");
        double warpX = Double.parseDouble(warpPositionSplit[0]);
        double warpY = Double.parseDouble(warpPositionSplit[1]);
        double warpZ = Double.parseDouble(warpPositionSplit[2]);
        return new Location(world, warpX, warpY, warpZ);
    }

    @Override
    public void run() {
        if (!(warpPosition() == null)) {
            Location warpPostion = warpPosition();
            world.getPlayers().forEach(player -> {
                if (player.getLocation().distance(warpPostion) < warpTPRadius && !hasEffect.contains(player)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 3));
                    hasEffect.add(player);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.removeScoreboardTag("warp");
                        Location location = player.getWorld().getSpawnLocation();
                        if (location.getX() < 0)
                            location.setX(location.getX() - 0.5);
                        else
                            location.setX(location.getX() + 0.5);
                        location.setY(location.getY() + 0.5);
                        if (location.getZ() < 0)
                            location.setZ(location.getZ() + 0.5);
                        else
                            location.setZ(location.getZ() - 0.5);
                        location.setDirection(player.getLocation().getDirection());
                        player.teleport(location);
                        player.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 60, 0.2, 0.5, 0.2, 0.1, null, true);
                        player.sendMessage("§bTeleported to spawn!");
                        hasEffect.remove(player);
                    }, 30);
                }
                if (warpProtectionEnabled) {
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.isOp()) return;
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

    @EventHandler
    private void onBlockBreak(EntityExplodeEvent event) {
        Bukkit.getLogger().info("Event called");
        if (!warpGriefProtectionEnabled || warpPosition() == null) return;
        if (event.getEntity().getLocation().distance(warpPosition()) < warpGriefProtectionRadius) {
            Bukkit.getLogger().info("Event cancelled");
            event.setCancelled(true);
        }
    }
}
