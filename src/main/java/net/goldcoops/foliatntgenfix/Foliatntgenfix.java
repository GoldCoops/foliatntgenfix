package net.goldcoops.foliatntgenfix;

import net.goldcoops.foliatntgenfix.commands.ForgeCommand;
import net.goldcoops.foliatntgenfix.commands.GiveDuplicatorCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Foliatntgenfix extends JavaPlugin {

    private DispenserListener dispenserListener;

    @Override
    public void onEnable() {
        DispenserStorage storage = new DispenserStorage(this);
        dispenserListener = new DispenserListener(this, storage);
        getCommand("forge").setExecutor(new ForgeCommand(this));
        getCommand("giveduplicator").setExecutor(new GiveDuplicatorCommand(this));
        getServer().getPluginManager().registerEvents(dispenserListener, this);
        getLogger().info("FoliaTntGenFix enabled.");
    }

    @Override
    public void onDisable() {
        if (dispenserListener != null) {
            dispenserListener.saveAll();
        }
        getLogger().info("FoliaTntGenFix disabled.");
    }



    public static ItemStack createTntDispenserItem() {
        ItemStack dispenser = new ItemStack(Material.DISPENSER);
        ItemMeta meta = dispenser.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "TNT Dispenser");
        meta.setLore(List.of(
                ChatColor.GRAY + "Place and power with redstone",
                ChatColor.GRAY + "to continuously fire lit TNT."
        ));
        meta.getPersistentDataContainer().set(ForgeCommand.TNT_DISPENSER_KEY, PersistentDataType.BYTE, (byte) 1);
        dispenser.setItemMeta(meta);
        return dispenser;
    }
}
