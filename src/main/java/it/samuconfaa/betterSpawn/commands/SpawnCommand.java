package it.samuconfaa.betterSpawn.commands;

import it.samuconfaa.betterSpawn.BetterSpawn;
import it.samuconfaa.betterSpawn.manager.ConfigManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor, TabCompleter {

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 1){
            List<String> subCommands = new ArrayList<>();

            if(sender.hasPermission("betterspawn.spawn")){
                subCommands.add("spawn");
            }
            if (sender.hasPermission("betterspawn.set")) {
                subCommands.add("set");
            }
            if (sender.hasPermission("betterspawn.reload")) {
                subCommands.add("reload");
            }
            return subCommands.stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
