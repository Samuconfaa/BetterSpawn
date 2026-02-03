package it.samuconfaa.betterSpawn.commands;

import it.samuconfaa.betterSpawn.BetterSpawn;
import it.samuconfaa.betterSpawn.manager.ConfigManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor, TabCompleter {

    private final BetterSpawn plugin;
    public SpawnCommand(BetterSpawn plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player p)){
            sender.sendMessage("Solo i giocatori possono usare questo comando!");
            return true;
        }
        if(args.length == 0){
            if(p.hasPermission("betterspawn.spawn")){
                if(plugin.getCooldowns().containsKey(p.getUniqueId())){
                    long now = System.currentTimeMillis();
                    long last = plugin.getCooldowns().get(p.getUniqueId());
                    long diff = now - last;
                    int delay = plugin.getConfigManager().getDelay();
                    if(diff < delay) {
                        long remaining = (delay - diff) / 1000;
                        p.sendMessage(plugin.getConfigManager().getNoDelayMessage().replace("%time%", String.valueOf(remaining)));
                        return true;
                    }
                }
                plugin.getCooldowns().put(p.getUniqueId(), System.currentTimeMillis());
                startCooldown(p, plugin.getConfigManager().getCooldown());
            }else{
                p.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
            }
            return true;
        }else if (args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                if(p.hasPermission("betterspawn.reload")){
                    plugin.getConfigManager().load();
                    p.sendMessage(plugin.getConfigManager().getReloadMessage());
                }else{
                    p.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
                }
                return true;
            }else if (args[0].equalsIgnoreCase("setspawn")){
                if(p.hasPermission("betterspawn.setspawn")){
                    Location loc = p.getLocation();
                    plugin.getConfigManager().setSpawn(loc);
                }else{
                    p.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
                }
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> commands = List.of("reload", "setspawn");
            org.bukkit.util.StringUtil.copyPartialMatches(args[0], commands, completions);
            return completions;
        }
        return List.of();
    }

    private void startCooldown(Player p, int seconds) {
        Location locIniziale = p.getLocation().clone();

        BukkitTask task = new BukkitRunnable() {
            int timeLeft = seconds;

            @Override
            public void run() {
                Location current = p.getLocation();

                // controllo movimento
                if (current.getBlockX() != locIniziale.getBlockX() ||
                        current.getBlockY() != locIniziale.getBlockY() ||
                        current.getBlockZ() != locIniziale.getBlockZ()) {

                    p.sendMessage(plugin.getConfigManager().getPlayerMovedMessage());
                    plugin.getCountdownTasks().remove(p.getUniqueId());
                    cancel(); // ferma il task se si muove
                    return;
                }

                if (timeLeft <= 0) {
                    p.teleport(plugin.getConfigManager().getSpawnLocation());
                    p.sendMessage(plugin.getConfigManager().getSuccessMessage());
                    plugin.getCountdownTasks().remove(p.getUniqueId());
                    plugin.getCooldowns().put(p.getUniqueId(), System.currentTimeMillis());
                    cancel(); //cancello il task se tp
                } else {
                    p.sendTitle(String.valueOf(timeLeft), "", 0, 20, 0);
                    timeLeft--;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        plugin.getCountdownTasks().put(p.getUniqueId(), task);
    }

}



