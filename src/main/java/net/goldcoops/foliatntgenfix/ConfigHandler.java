package net.goldcoops.foliatntgenfix;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private final JavaPlugin plugin;

    private final Foliatntgenfix main;

    public ConfigHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.main = Foliatntgenfix.getInstance();
    }

    public void setMaps(Map<Material, Integer> map, String path) {
        if (plugin.getConfig().contains(path)) plugin.getConfig().set(path, null);
        ConfigurationSection section = plugin.getConfig().createSection(path);
        for (Map.Entry<Material, Integer> entry : map.entrySet()) {
            section.set(entry.getKey().name(), entry.getValue());
        }
        plugin.saveConfig();
    }

    public int getTntCooldown(int level) {
        String path = level + "-tier-delay";
        return plugin.getConfig().getInt(path);
    }


    public Map<Material, Integer> getMaterialMap(String path) {
        Map<Material, Integer> map = new HashMap<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
        if (section == null) return null;

        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);

            if (material == null) {
                plugin.getLogger().warning("Invalid material at " + path + ": " + key);
                continue;
            }

            map.put(material, section.getInt(key));
        }

        return map;
    }
}
