package net.goldcoops.foliatntgenfix.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


import java.util.Map;

import static net.goldcoops.foliatntgenfix.Foliatntgenfix.REQUIRED_ITEMS1;
import static net.goldcoops.foliatntgenfix.Foliatntgenfix.REQUIRED_ITEMS2;
import static net.goldcoops.foliatntgenfix.Foliatntgenfix.createTntDispenserItem;

public class ForgeCommand implements CommandExecutor {


    private final JavaPlugin plugin;

    public ForgeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }







    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        int level;
        try {
            level = (args.length == 0) ? 1 : Integer.parseInt(args[0]);
            if (level < 1 || level > 2) throw new NumberFormatException();
        }catch (NumberFormatException e) {
            player.sendMessage("Invalid level. Level must be 1 or 2.");
            return false;
        }
        Inventory inv = player.getInventory();
        Map<Material, Integer> requiredItems = (level == 1) ? REQUIRED_ITEMS1 : REQUIRED_ITEMS2;


        if (missingCheck(requiredItems, player, inv)) return true;


        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            inv.removeItem(new ItemStack(entry.getKey(), entry.getValue()));
        }


        ItemStack dispenser = createTntDispenserItem((byte) level);
        player.getInventory().addItem(dispenser);
        player.sendMessage(ChatColor.GREEN + "Forged a TNT Dispenser!");
        return true;


    }


    private boolean missingCheck(Map<Material, Integer> requiredItems, Player player, Inventory inv) {


        StringBuilder missing_sb = new StringBuilder();
        StringBuilder required_sb = new StringBuilder();
        required_sb.append(ChatColor.GRAY + "Required: ");
        boolean missingItems = false;
        int passses = 0;

        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            if (passses > 0) required_sb.append(", ");
            required_sb.append(ChatColor.GRAY + entry.getValue().toString() + "x " + formatName(entry.getKey()));
            if (!inv.containsAtLeast(new ItemStack(entry.getKey()), entry.getValue())) {
                missing_sb.append(ChatColor.RED + "Missing: " + entry.getValue() + "x " + formatName(entry.getKey()) + "\n");
                missingItems = true;
            }
            passses++;
        }

        if (missingItems) {
            player.sendMessage(missing_sb.toString());
            player.sendMessage(required_sb.toString().trim());
            return true;
        }
        return false;
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
