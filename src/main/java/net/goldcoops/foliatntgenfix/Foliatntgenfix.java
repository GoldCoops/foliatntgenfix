package net.goldcoops.foliatntgenfix;

import net.goldcoops.foliatntgenfix.commands.ForgeCommand;
import net.goldcoops.foliatntgenfix.commands.GiveDuplicatorCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
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



    public static Map<Material, Integer> REQUIRED_ITEMS1;

    public static Map<Material, Integer> REQUIRED_ITEMS2;

    @Override
    public void onEnable() {
        dispenserListener = new DispenserListener(this);
        configHandler = new ConfigHandler(this);
        saveDefaultConfig();
        REQUIRED_ITEMS1 = configHandler.getMaterialMap("first-tier");
        REQUIRED_ITEMS2 = configHandler.getMaterialMap("second-tier");
        getCommand("forge").setExecutor(new ForgeCommand(this));
        getCommand("giveduplicator").setExecutor(new GiveDuplicatorCommand(this));
        getServer().getPluginManager().registerEvents(dispenserListener, this);
        getLogger().info("FoliaTntGenFix enabled.");

    }

    @Override
    public void onDisable() {
        getLogger().info("FoliaTntGenFix disabled.");
    }



    public static ItemStack createTntDispenserItem(byte level) {
        ItemStack dispenser = new ItemStack(Material.DISPENSER);
        ItemMeta meta = dispenser.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "TNT Duplicator");
        meta.setLore(List.of(
                ChatColor.GRAY + "Place and power with redstone clock",
                ChatColor.GRAY + "to continually spawn infinite TNT"
        ));
        meta.getPersistentDataContainer().set(TNT_DISPENSER_KEY, PersistentDataType.BYTE, level);
        dispenser.setItemMeta(meta);
        return dispenser;
    }

    public static byte getTntDispenserBlockLevel(Block block) {
        if (block.getType() != Material.DISPENSER) return 0;
        if (!(block.getState() instanceof Dispenser dispenser)) return 0;
        if (dispenser.getPersistentDataContainer().isEmpty()) return 0;
        if (isTntDispenserBlock(block)) {
            return dispenser.getPersistentDataContainer().get(TNT_DISPENSER_KEY, PersistentDataType.BYTE);
        }
        return 0;
    }


    public static boolean isTntDispenserBlock(Block block) {
        if (block.getType() != Material.DISPENSER) return false;
        if (!(block.getState() instanceof Dispenser dispenser)) return false;
        if (dispenser.getPersistentDataContainer().isEmpty()) return false;
        return dispenser.getPersistentDataContainer().has(TNT_DISPENSER_KEY, PersistentDataType.BYTE);
    }

    public static boolean isTntDispenserItem(ItemStack item) {
        if (item == null || item.getType() != Material.DISPENSER) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(TNT_DISPENSER_KEY, PersistentDataType.BYTE);
    }



    public static Location toBlockLocation(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
