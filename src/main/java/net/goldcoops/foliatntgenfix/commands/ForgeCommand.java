package net.goldcoops.foliatntgenfix.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

import static net.goldcoops.foliatntgenfix.Foliatntgenfix.createTntDispenserItem;

public class ForgeCommand implements CommandExecutor {


    private final JavaPlugin plugin;

    public ForgeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public static final NamespacedKey TNT_DISPENSER_KEY = new NamespacedKey("foliatntgenfix", "tnt_dispenser");

    private static final Map<Material, Integer> REQUIRED_ITEMS = Map.of(
        Material.DISPENSER, 1,
        Material.TNT, 1,
        Material.REDSTONE, 1,
        Material.SLIME_BLOCK, 1
    );


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Inventory inv = player.getInventory();

        StringBuilder sb = new StringBuilder();

        boolean missingItems = false;
        for (Map.Entry<Material, Integer> entry : REQUIRED_ITEMS.entrySet()) {
            if (!inv.containsAtLeast(new ItemStack(entry.getKey()), entry.getValue())) {
                sb.append(ChatColor.RED + "Missing: " + entry.getValue() + "x " + formatName(entry.getKey()) + "\n");
                //player.sendMessage(ChatColor.RED + "Missing: " + entry.getValue() + "x " + formatName(entry.getKey()));
                missingItems = true;
            }
        }
        if (missingItems) {
            player.sendMessage(sb.toString());
            player.sendMessage(ChatColor.GRAY + "Required: 1x Dispenser, 1x TNT, 1x Redstone, 1x Slime Block");
            return true;
        }


        for (Map.Entry<Material, Integer> entry : REQUIRED_ITEMS.entrySet()) {
            inv.removeItem(new ItemStack(entry.getKey(), entry.getValue()));
        }


        ItemStack dispenser = createTntDispenserItem(1);
        player.getInventory().addItem(dispenser);
        player.sendMessage(ChatColor.GREEN + "Forged a TNT Dispenser!");
        return true;
    }

    private String formatName(Material mat) {
        String name = mat.name().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            sb.append(Character.toUpperCase(word.charAt(0)))
              .append(word.substring(1).toLowerCase())
              .append(" ");
        }
        return sb.toString().trim();
    }
}
