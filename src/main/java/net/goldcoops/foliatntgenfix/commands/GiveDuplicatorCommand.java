package net.goldcoops.foliatntgenfix.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static net.goldcoops.foliatntgenfix.Foliatntgenfix.createTntDispenserItem;

public class GiveDuplicatorCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public GiveDuplicatorCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {



        if (args.length > 0) {

            ItemStack tntduper = createTntDispenserItem((byte) 1);

            String targetName = args[0];

            Player player = plugin.getServer().getPlayerExact(targetName);

            if (player == null || !player.isOnline()) {
                commandSender.sendMessage("Player not found.");
                return false;
            }

            if (args.length == 2) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    if (amount < 1) {
                        throw new NumberFormatException();
                    }
                    tntduper.setAmount(amount);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage("Invalid amount.");
                    return false;
                }
            } else {
                tntduper.setAmount(1);
            }


            player.getInventory().addItem(tntduper);
            return true;
        } else {
            return false;
        }


    }
}