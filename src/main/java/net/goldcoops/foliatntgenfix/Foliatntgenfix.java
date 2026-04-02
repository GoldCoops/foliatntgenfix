package net.goldcoops.foliatntgenfix;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.entity.Player;

public final class Foliatntgenfix extends JavaPlugin {
    RegionScheduler scheduler = getServer().getRegionScheduler();
    private static Plugin instance;

    public static Plugin getInstance() {
        return instance;
    }






    // Inside a listener or command
    public void onPlayerAction(Player player) {
        World currentWorld = player.getWorld();
        String worldName = currentWorld.getName();
        player.sendMessage("You are in world: " + worldName);
    }



    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    @Override
    public void onEnable() {
        instance = this;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
