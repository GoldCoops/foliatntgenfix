package net.goldcoops.foliatntgenfix;

import net.goldcoops.foliatntgenfix.commands.ForgeCommand;
import net.goldcoops.foliatntgenfix.commands.GiveDuplicatorCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public final class Foliatntgenfix extends JavaPlugin {

    private DispenserListener dispenserListener;
    public ConfigHandler configHandler;
    public static final NamespacedKey TNT_DISPENSER_KEY = new NamespacedKey("foliatntgenfix", "tnt_dispenser");


    public static Map<Material, Integer> REQUIRED_ITEMS1 = Map.of(
            Material.DISPENSER, 1,
            Material.TNT, 1,
            Material.REDSTONE, 1,
            Material.SLIME_BLOCK, 1
    );

    public static Map<Material, Integer> REQUIRED_ITEMS2 = Map.of(
            Material.DISPENSER, 1,
            Material.TNT, 1,
            Material.REDSTONE, 1,
            Material.SLIME_BLOCK, 1
    );

    @Override
    public void onEnable() {
        dispenserListener = new DispenserListener(this);
        configHandler = new ConfigHandler(this);
        saveDefaultConfig();
        getCommand("forge").setExecutor(new ForgeCommand(this));
        getCommand("giveduplicator").setExecutor(new GiveDuplicatorCommand(this));
        getServer().getPluginManager().registerEvents(dispenserListener, this);
        getLogger().info("FoliaTntGenFix enabled.");

    }

    @Override
    public void onDisable() {
        getLogger().info("FoliaTntGenFix disabled.");
    }



    public static ItemStack createTntDispenserItem(int level) {
        ItemStack dispenser = new ItemStack(Material.DISPENSER);
        ItemMeta meta = dispenser.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "TNT Duplicator");
        meta.setLore(List.of(
                ChatColor.GRAY + "Place and power with redstone clock",
                ChatColor.GRAY + "to continually spawn infinite TNT"
        ));
        meta.getPersistentDataContainer().set(TNT_DISPENSER_KEY, PersistentDataType.BYTE, (byte) 1);
        dispenser.setItemMeta(meta);
        return dispenser;
    }


    public static Location toBlockLocation(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
