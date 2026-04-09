package net.goldcoops.foliatntgenfix.commands;

import net.goldcoops.foliatntgenfix.ConfigHandler;
import net.goldcoops.foliatntgenfix.Foliatntgenfix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import static net.goldcoops.foliatntgenfix.Foliatntgenfix.*;



public class ReloadCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final ConfigHandler configHandler;
    private final Foliatntgenfix main;

    public ReloadCommand(JavaPlugin plugin, ConfigHandler configHandler) {
        this.plugin = plugin;
        this.configHandler = configHandler;
        this.main = Foliatntgenfix.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {


        if (!sender.hasPermission("foliatntgenfix.reload")) return false;
        plugin.reloadConfig();


        REQUIRED_ITEMS1 = configHandler.getMaterialMap("first-tier");
        REQUIRED_ITEMS2 = configHandler.getMaterialMap("second-tier");

        COOLDOWN.clear();
        for (int i = 1; i <= levels; i++) {
            COOLDOWN.add(configHandler.getTntCooldown(i));
        }

        sender.sendMessage("Config Reloaded.");

        return true;
    }
}
