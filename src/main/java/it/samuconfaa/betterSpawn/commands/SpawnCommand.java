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

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final HashMap<UUID, BukkitTask> countdownTasks = new HashMap<>();

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

    private void startCooldown(Player p, int seconds){
        Location locIniziale = p.getLocation().clone(); //posizione iniziale
        final BukkitTask[] taskHolder = new BukkitTask[1]; //per eliminare il task, dentro la lambda deve essere final
        final int[] timeLeft = {seconds}; //final perchè richiesto dalla lambda. traccia quanto rimane

        taskHolder[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            Location current = p.getLocation();
            if (current.getBlockX() != locIniziale.getBlockX() ||
                current.getBlockY() != locIniziale.getBlockY() ||
                current.getBlockZ() != locIniziale.getBlockZ()){
                p.sendMessage(plugin.getConfigManager().getPlayerMovedMessage());
                countdownTasks.remove(p.getUniqueId()); //elimino il task dall'hashmap
                taskHolder[0].cancel();
                return;
            }
            if(timeLeft[0] <= 0){ //se ho aspettato X secondi
                p.teleport(plugin.getConfigManager().getSpawnLocation()); //p teletrasportato
                p.sendMessage(plugin.getConfigManager().getSuccessMessage()); //messaggio di successo
                countdownTasks.remove(p.getUniqueId()); //elimino il task
                cooldowns.put(p.getUniqueId(), System.currentTimeMillis()); //aggiorno hashmap del cooldwon
                taskHolder[0].cancel();
            }else{ //se non ho ancora aspettato X secondi
                p.sendTitle(String.valueOf(timeLeft[0]), "", 0, 20, 0); //messaggio a schermo
                timeLeft[0]--; //contratore decrementato
            }
        }, 0L, 20L);

        countdownTasks.put(p.getUniqueId(), taskHolder[0]); //lo salvo per annullarlo se il player si muove
    }

    public HashMap<UUID, BukkitTask> getCountdownTasks() {
        return countdownTasks;
    }

    public HashMap<UUID, Long> getCooldowns() {
        return cooldowns;
    }


}
