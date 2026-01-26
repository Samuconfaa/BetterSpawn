package it.samuconfaa.betterSpawn.manager;

import it.samuconfaa.betterSpawn.BetterSpawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class ConfigManager {
    private BetterSpawn plugin;

    public ConfigManager(BetterSpawn plugin) {
        this.plugin = plugin;
    }

    private int delay;
    private int cooldown;
    private String success;
    private String noPermission;
    private String noDelay;
    private String reload;
    private String setSpawnSuccess;
    private String playerMoved;
    private Location location;

    public void load(){
        plugin.reloadConfig();

        delay = plugin.getConfig().getInt("delay");
        cooldown = plugin.getConfig().getInt("teleport-cooldown");
        success = getConfigString("messages.success");
        noPermission = getConfigString("messages.no-permission");
        noDelay = getConfigString("messages.no-delay");
        setSpawnSuccess = getConfigString("messages.setspawn-success");
        reload = getConfigString("messages.reload");
        playerMoved = getConfigString("messages.player-moved");

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

    public int getCooldown(){
        return cooldown;
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

    public String getPlayerMovedMessage(){
        return playerMoved;
    }

    public Location loadLocation(){
        double x = plugin.getConfig().getInt("location.x");
        double y = plugin.getConfig().getInt("location.y");
        double z = plugin.getConfig().getInt("location.z");
        float yaw = (float) plugin.getConfig().getInt("location.yaw");
        float pitch = (float) plugin.getConfig().getInt("location.pitch");
        String world = plugin.getConfig().getString("location.world");
        return new Location(plugin.getServer().getWorld(world), x, y, z, yaw, pitch);
    }

    public Location getSpawnLocation(){
        return location;
    }

    public void setSpawn(Location location){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getConfig().set("location.x", location.getBlockX());
            plugin.getConfig().set("location.y", location.getBlockY());
            plugin.getConfig().set("location.z", location.getBlockZ());
            plugin.getConfig().set("location.yaw", location.getYaw());
            plugin.getConfig().set("location.pitch", location.getPitch());
            plugin.getConfig().set("location.world", location.getWorld().getName());
            plugin.saveConfig();
            load();
        });
    }

}
