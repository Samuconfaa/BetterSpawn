package it.samuconfaa.betterSpawn.listeners;

import it.samuconfaa.betterSpawn.BetterSpawn;
import it.samuconfaa.betterSpawn.commands.SpawnCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final BetterSpawn plugin;

    public PlayerQuitListener(BetterSpawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        UUID uuid = e.getPlayer().getUniqueId();

        if(plugin.getCountdownTasks().containsKey(uuid)){
            plugin.getCountdownTasks().get(uuid).cancel();
            plugin.getCountdownTasks().remove(uuid);
        }

        if(plugin.getCooldowns().containsKey(uuid)){
            plugin.getCooldowns().remove(uuid);
        }
    }
}
