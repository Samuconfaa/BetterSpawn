package it.samuconfaa.betterSpawn;

import it.samuconfaa.betterSpawn.commands.SpawnCommand;
import it.samuconfaa.betterSpawn.listeners.PlayerQuitListener;
import it.samuconfaa.betterSpawn.manager.ConfigManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public final class BetterSpawn extends JavaPlugin {

    private static BetterSpawn instance;
    private ConfigManager configManager;

    @Getter // Lombok genera automaticamente getCountdownTasks()
    private final HashMap<UUID, BukkitTask> countdownTasks = new HashMap<>();

    @Getter // Lombok genera automaticamente getCooldowns()
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.load();

        getCommand("spawn").setExecutor(new SpawnCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        getLogger().info("Plugin BetterSpawn enabled!");
    }

    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("Plugin BetterSpawn disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static BetterSpawn getInstance() {
        return instance;
    }
}
