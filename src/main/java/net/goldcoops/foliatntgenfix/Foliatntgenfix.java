package net.goldcoops.foliatntgenfix;

import org.bukkit.plugin.java.JavaPlugin;

public final class Foliatntgenfix extends JavaPlugin {

    private DispenserListener dispenserListener;

    @Override
    public void onEnable() {
        DispenserStorage storage = new DispenserStorage(this);
        dispenserListener = new DispenserListener(this, storage);
        getCommand("forge").setExecutor(new ForgeCommand(this));
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
}
