package it.samuconfaa.betterSpawn.manager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private int delay;
    private String success;
    private String noPermission;
    private String noDelay;
    private String reload;
    private String setSpawnSuccess;
    private Location location;

    public void load(){
        plugin.reloadConfig();

        delay = plugin.getConfig().getInt("delay");
        success = getConfigString("messages.success");
        noPermission = getConfigString("messages.no-permission");
        noDelay = getConfigString("messages.no-delay");
        setSpawnSuccess = getConfigString("messages.setspawn-success");
        reload = getConfigString("messages.reload");

        location = loadLocation();
    }

    private String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String getConfigString(String path){
        return color(plugin.getConfig().getString(path));
    }

    public String getSuccessMessage(){
        return success;
    }

    public int getDelay(){
        return delay;
    }

    public String getNoPermissionMessage(){
        return noPermission;
    }

    public String getNoDelayMessage(){
        return noDelay;
    }

    public String getReloadMessage(){
        return reload;
    }

    public String getSetSpawnSuccessMessage(){
        return setSpawnSuccess;
    }

    public Location loadLocation(){
        int x = plugin.getConfig().getInt("location.x");
        int y = plugin.getConfig().getInt("location.y");
        int z = plugin.getConfig().getInt("location.z");
        String world = plugin.getConfig().getString("location.world");
        return new Location(plugin.getServer().getWorld(world), x, y, z);
    }

    public Location getSpawnLocation(){
        return location;
    }

    public void setSpawn(Location location){
        plugin.getConfig().set("location.x", location.getBlockX());
        plugin.getConfig().set("location.y", location.getBlockY());
        plugin.getConfig().set("location.z", location.getBlockZ());
        plugin.getConfig().set("location.world", location.getWorld().getName());
        plugin.saveConfig();
    }

}
