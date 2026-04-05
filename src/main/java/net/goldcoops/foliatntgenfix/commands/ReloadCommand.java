package net.goldcoops.foliatntgenfix.commands;

import net.goldcoops.foliatntgenfix.Foliatntgenfix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import static net.goldcoops.foliatntgenfix.Foliatntgenfix.*;



public class ReloadCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        plugin.reloadConfig();


        REQUIRED_ITEMS1 = configHandler.getMaterialMap("first-tier");
        REQUIRED_ITEMS2 = configHandler.getMaterialMap("second-tier");


        sender.sendMessage("Config Reloaded.");

        return true;
    }
}
