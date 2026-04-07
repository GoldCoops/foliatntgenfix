package net.goldcoops.foliatntgenfix.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.format.NamedTextColor;


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
        if (!player.hasPermission("foliatntgenfix.forge")) return false;
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
        player.sendMessage(Component.text("Forged a TNT Dispenser!", NamedTextColor.GREEN));
        return true;


    }


    private boolean missingCheck(Map<Material, Integer> requiredItems, Player player, Inventory inv) {


        StringBuilder missing_sb = new StringBuilder();
        StringBuilder required_sb = new StringBuilder();
        required_sb.append("Required: ");
        boolean missingItems = false;
        int passses = 0; // I know this is a bad way to do it, but im tired and it works

        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            if (passses > 0) required_sb.append(", ");
            required_sb.append(entry.getValue().toString()).append("x ").append(formatName(entry.getKey()));
            if (!inv.containsAtLeast(new ItemStack(entry.getKey()), entry.getValue())) {
                missing_sb.append("Missing: ").append(entry.getValue()).append("x ").append(formatName(entry.getKey())).append("\n");
                missingItems = true;
            }
            passses++;
        }

        if (missingItems) {
            player.sendMessage(Component.text(missing_sb.toString().trim(), NamedTextColor.RED));
            player.sendMessage(Component.text(required_sb.toString().trim(), NamedTextColor.GRAY));
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
