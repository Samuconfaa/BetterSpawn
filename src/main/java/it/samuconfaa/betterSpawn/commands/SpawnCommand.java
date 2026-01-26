package it.samuconfaa.betterSpawn.commands;

import it.samuconfaa.betterSpawn.BetterSpawn;
import it.samuconfaa.betterSpawn.manager.ConfigManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor {
    private final BetterSpawn plugin;
    public SpawnCommand(BetterSpawn plugin) {
        this.plugin = plugin;
    }

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player p)){
            sender.sendMessage("Solo i giocatori possono usare questo comando!");
            return true;
        }
        if(args.length == 0){
            if(p.hasPermission("betterspawn.spawn")){
                if(cooldowns.containsKey(p.getUniqueId())){
                    long now = System.currentTimeMillis();
                    long last = cooldowns.get(p.getUniqueId());
                    long diff = now - last;
                    int delay = plugin.getConfigManager().getDelay();
                    if(diff < delay) {
                        long remaining = (delay - diff) / 1000;
                        p.sendMessage(plugin.getConfigManager().getNoDelayMessage().replace("%time%", String.valueOf(remaining)));
                        return true;
                    }
                }
                cooldowns.put(p.getUniqueId(), System.currentTimeMillis());
                p.teleport(plugin.getConfigManager().getSpawnLocation());
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
}
