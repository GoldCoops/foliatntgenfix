package net.goldcoops.foliatntgenfix;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DispenserStorage {

    private final File file;
    private final YamlConfiguration config;

    public DispenserStorage(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "dispensers.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public Set<Location> load() {
        Set<Location> locations = new HashSet<>();
        List<String> entries = config.getStringList("dispensers");
        for (String entry : entries) {
            Location loc = deserialize(entry);
            if (loc != null) {
                locations.add(loc);
            }
        }
        return locations;
    }

    public void save(Set<Location> locations) {
        List<String> entries = new ArrayList<>();
        for (Location loc : locations) {
            entries.add(serialize(loc));
        }
        config.set("dispensers", entries);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String serialize(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private Location deserialize(String entry) {
        String[] parts = entry.split(",");
        if (parts.length != 4) return null;
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;
        try {
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
