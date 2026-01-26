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
    private final HashMap<UUID, BukkitTask> countdownTasks;
    private final HashMap<UUID, Long> cooldowns;

    public PlayerQuitListener(HashMap<UUID, BukkitTask> countdownTasks, HashMap<UUID, Long> cooldowns) {
        this.countdownTasks = countdownTasks;
        this.cooldowns = cooldowns;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        UUID uuid = e.getPlayer().getUniqueId();

        if(countdownTasks.containsKey(uuid)){
            countdownTasks.get(uuid).cancel();
            countdownTasks.remove(uuid);
        }

        if(cooldowns.containsKey(uuid)){
            cooldowns.remove(uuid);
        }
    }
}
