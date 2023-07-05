package de.ferargent.minecraftTringus;
import de.ferargent.minecraftTringus.commands.spawn;
import de.ferargent.minecraftTringus.commands.sealSpawn;
import de.ferargent.minecraftTringus.commands.setWarp;
import de.ferargent.minecraftTringus.listeners.ElytraListener;
import de.ferargent.minecraftTringus.listeners.SealSpawnListener;
import de.ferargent.minecraftTringus.listeners.SpawnProtectionListener;

import de.ferargent.minecraftTringus.listeners.WarpListener;
import org.bukkit.plugin.java.JavaPlugin;


public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getCommand("spawn").setExecutor(new spawn());
        getCommand("sealspawn").setExecutor(new sealSpawn());
        getCommand("setwarp").setExecutor(new setWarp());
        getCommand("sealspawn").setTabCompleter(new commandTabComplete());
        getCommand("setwarp").setTabCompleter(new commandTabComplete());
        getServer().getPluginManager().registerEvents(ElytraListener.create(this), this);
        getServer().getPluginManager().registerEvents(SpawnProtectionListener.create(this), this);
        getServer().getPluginManager().registerEvents(SealSpawnListener.create(this), this);
        getServer().getPluginManager().registerEvents(WarpListener.create(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }
}
