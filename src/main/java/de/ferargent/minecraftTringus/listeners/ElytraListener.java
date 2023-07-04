package de.ferargent.minecraftTringus.listeners;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ElytraListener extends BukkitRunnable implements Listener {

    private final Plugin plugin;
    private final int spawnRadius;
    private final boolean boostEnabled;
    private final List<Player> hasElytra = new ArrayList<>();
    private final World world = Bukkit.getWorld("world");

    private ElytraListener(Plugin plugin, int spawnRadius, boolean boostEnabled) {
        this.plugin = plugin;
        this.spawnRadius = spawnRadius;
        this.boostEnabled = boostEnabled;

        this.runTaskTimer(this.plugin, 0, 3);
    }

    public static ElytraListener create(Plugin plugin) {
        var config = plugin.getConfig();
        if (!config.contains("spawnRadius") || !config.contains("boostEnabled")) {
            plugin.saveResource("config.yml", true);
            plugin.reloadConfig();
        }
        return new ElytraListener(plugin, config.getInt("spawnRadius"), config.getBoolean("boostEnabled"));
    }

    private ItemStack customElytra() {
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        elytra.addEnchantment(org.bukkit.enchantments.Enchantment.BINDING_CURSE, 1);
        ItemMeta meta = elytra.getItemMeta();
        meta.setUnbreakable(true);
        meta.setDisplayName("§b§lEinweg-Elytra");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        elytra.setItemMeta(meta);
        return elytra;
    }

    private ItemStack customBoost() {
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta meta = firework.getItemMeta();
        meta.setDisplayName("§c§lBoost");
        firework.setItemMeta(meta);
        return firework;
    }

    @Override
    public void run() {
        world.getPlayers().forEach(player -> {
            if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE && player.getInventory().getChestplate() != null)
                return;
            if (isInSpawnRadius(player) && !hasElytra.contains(player) && boostEnabled && player.getInventory().getItem(8) == null) {
                player.getInventory().setItem(8, customBoost());
            }
            if (player.getInventory().contains(customElytra()) && !hasElytra.contains(player)) {
                hasElytra.add(player);
            }
            if (isInSpawnRadius(player) && !hasElytra.contains(player) && player.getInventory().getChestplate() == null) {
                if (player.getAdvancementProgress(Bukkit.getAdvancement(NamespacedKey.fromString("end/elytra"))).isDone()) {
                    player.getInventory().setChestplate(customElytra());
                } else {
                    player.getInventory().setChestplate(customElytra());
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.getAdvancementProgress(Bukkit.getAdvancement(NamespacedKey.minecraft("end/elytra"))).revokeCriteria("elytra");
                    }, 5);
                }
                hasElytra.add(player);
            }
            if (hasElytra.contains(player) && player.isOnGround() && !isInSpawnRadius(player)) {
                player.getInventory().setChestplate(null);
                player.getInventory().setItem(8, new ItemStack(Material.AIR));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    hasElytra.remove(player);
                }, 5);
            }
        });
    }

    @EventHandler
    private void onBoost(PlayerElytraBoostEvent event) {
        Player player = event.getPlayer();
        if (boostEnabled && hasElytra.contains(player)) {
            player.getInventory().setItem(8, customBoost());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER
                && (event.getCause() == EntityDamageEvent.DamageCause.FALL
                || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL)
                && hasElytra.contains(event.getEntity())) event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent moveEvent) {
        Player player = moveEvent.getPlayer();
        if (!hasElytra.contains(player) || !player.isGliding()) return;
        player.getWorld().spawnParticle(Particle.CHERRY_LEAVES, player.getEyeLocation().add(1, 0, 0), 0, 0.1, 0.1, 0.1, 1);
        player.getWorld().spawnParticle(Particle.CHERRY_LEAVES, player.getEyeLocation().add(-1, 0, 0), 0, 0.1, 0.1, 0.1, 1);
    }

    private boolean isInSpawnRadius(Player player) {
        if (!player.getWorld().equals(world)) return false;
        return world.getSpawnLocation().distance(player.getLocation()) <= spawnRadius;
    }
}
