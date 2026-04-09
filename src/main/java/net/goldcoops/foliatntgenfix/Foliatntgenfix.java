package net.goldcoops.foliatntgenfix;

import net.goldcoops.foliatntgenfix.commands.ForgeCommand;
import net.goldcoops.foliatntgenfix.commands.GiveDuplicatorCommand;
import net.goldcoops.foliatntgenfix.commands.ReloadCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.command.brigadier.*;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class Foliatntgenfix extends JavaPlugin {

    private DispenserListener dispenserListener;
    public ConfigHandler configHandler;
    public static final NamespacedKey TNT_DISPENSER_KEY = new NamespacedKey("foliatntgenfix", "tnt_dispenser");
    public static final NamespacedKey TNT_TIMER_KEY = new NamespacedKey("foliatntgenfix", "tnt_timer");

    private static Foliatntgenfix instance;


    public static Map<Material, Integer> REQUIRED_ITEMS1;

    public static Map<Material, Integer> REQUIRED_ITEMS2;

    public static ArrayList<Integer> COOLDOWN;

    public static final int levels = 2;

    @Override
    public void onEnable() {
        instance = this;
        dispenserListener = new DispenserListener(this);
        configHandler = new ConfigHandler(this);
        saveDefaultConfig();
        REQUIRED_ITEMS1 = configHandler.getMaterialMap("first-tier");
        REQUIRED_ITEMS2 = configHandler.getMaterialMap("second-tier");
        for (int i = 1; i <= levels; i++) {COOLDOWN.add(configHandler.getTntCooldown(i));}
        getCommand("reloadrecipes").setExecutor(new ReloadCommand(this, configHandler));
        getCommand("forge").setExecutor(new ForgeCommand(this));
        getCommand("giveduplicator").setExecutor(new GiveDuplicatorCommand(this));
        getServer().getPluginManager().registerEvents(dispenserListener, this);
        getLogger().info("FoliaTntGenFix enabled.");

    }

    public static Foliatntgenfix getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        getLogger().info("FoliaTntGenFix disabled.");
    }



    public ItemStack createTntDispenserItem(byte level) {
        ItemStack dispenser = new ItemStack(Material.DISPENSER);
        ItemMeta meta = dispenser.getItemMeta();
        meta.displayName(Component.text("TNT Duplicator").color(NamedTextColor.GOLD));
        meta.lore(List.of(
                Component.text("Place and power with redstone clock").color(NamedTextColor.GRAY),
                Component.text( "to continually spawn infinite TNT").color(NamedTextColor.GRAY),
                Component.text( "Level: " + level).color(NamedTextColor.GRAY)
        ));
        meta.getPersistentDataContainer().set(TNT_DISPENSER_KEY, PersistentDataType.BYTE, level);
        dispenser.setItemMeta(meta);
        return dispenser;
    }

    public byte getTntDispenserBlockLevel(Block block) {
        if (block.getType() != Material.DISPENSER) return 0;
        if (!(block.getState() instanceof Dispenser dispenser)) return 0;
        if (dispenser.getPersistentDataContainer().isEmpty()) return 0;
        if (isTntDispenserBlock(block)) {
            return dispenser.getPersistentDataContainer().get(TNT_DISPENSER_KEY, PersistentDataType.BYTE);
        }
        return 0;
    }

    public byte getTntDispenserItemLevel(ItemStack item) {
        if (item.getType() != Material.DISPENSER) return 0;
        if (item.getItemMeta().getPersistentDataContainer().isEmpty()) return 0;
        if (isTntDispenserItem(item)) {
            return item.getItemMeta().getPersistentDataContainer().get(TNT_DISPENSER_KEY, PersistentDataType.BYTE);
        }
        return 0;
    }


    public boolean isTntDispenserBlock(Block block) {
        if (block.getType() != Material.DISPENSER) return false;
        if (!(block.getState() instanceof Dispenser dispenser)) return false;
        if (dispenser.getPersistentDataContainer().isEmpty()) return false;
        return dispenser.getPersistentDataContainer().has(TNT_DISPENSER_KEY, PersistentDataType.BYTE);
    }

    public boolean isTntDispenserItem(ItemStack item) {
        if (item == null || item.getType() != Material.DISPENSER) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(TNT_DISPENSER_KEY, PersistentDataType.BYTE);
    }


    public Location toBlockLocation(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
