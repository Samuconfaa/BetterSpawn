package it.samuconfaa.betterSpawn;

import it.samuconfaa.betterSpawn.commands.SpawnCommand;
import it.samuconfaa.betterSpawn.listeners.PlayerQuitListener;
import it.samuconfaa.betterSpawn.manager.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterSpawn extends JavaPlugin {

    private ConfigManager configManager;
    private SpawnCommand spawnCommand;

    @Override
    public void onEnable() {
        System.out.println("BetterSpawn enabled!");
        configManager = new ConfigManager(this);
        getCommand("spawn").setExecutor(new SpawnCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerQuitListener(spawnCommand.getCountdownTasks(), spawnCommand.getCooldowns()), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
