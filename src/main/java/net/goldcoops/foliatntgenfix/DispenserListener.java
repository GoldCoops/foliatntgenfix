package net.goldcoops.foliatntgenfix;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DispenserListener implements Listener {

    private final JavaPlugin plugin;
    private final DispenserStorage storage;
    // Locations where a custom TNT dispenser block has been placed
    private final Set<Location> placedDispenserLocations;
    // Active scheduled tasks keyed by location
    private final Map<Location, ScheduledTask> activeTasks = new HashMap<>();

    public DispenserListener(JavaPlugin plugin, DispenserStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.placedDispenserLocations = storage.load();
        plugin.getLogger().info("Loaded " + placedDispenserLocations.size() + " TNT dispenser location(s).");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!isTntDispenserItem(item)) return;
        placedDispenserLocations.add(event.getBlockPlaced().getLocation());
        storage.save(placedDispenserLocations);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (!placedDispenserLocations.remove(loc)) return;
        cancelTask(loc);
        storage.save(placedDispenserLocations);
    }

    @EventHandler
    public void onRedstoneChange(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.DISPENSER) return;

        Location loc = block.getLocation();
        if (!placedDispenserLocations.contains(loc)) return;

        if (event.getNewCurrent() > 0 && !activeTasks.containsKey(loc)) {
            ScheduledTask task = plugin.getServer().getRegionScheduler().runAtFixedRate(
                plugin,
                loc,
                scheduledTask -> fireTnt(loc),
                1L,
                10L
            );
            activeTasks.put(loc, task);
        } else if (event.getNewCurrent() == 0) {
            cancelTask(loc);
        }
    }

    public void saveAll() {
        activeTasks.forEach((loc, task) -> task.cancel());
        activeTasks.clear();
        storage.save(placedDispenserLocations);
    }

    private void cancelTask(Location loc) {
        ScheduledTask task = activeTasks.remove(loc);
        if (task != null) {
            task.cancel();
        }
    }

    private void fireTnt(Location dispenserLoc) {
        if (dispenserLoc.getWorld() == null) return;
        Location spawnLoc = dispenserLoc.clone().add(0.5, 1.0, 0.5);
        dispenserLoc.getWorld().spawnEntity(spawnLoc, EntityType.TNT);
    }

    private boolean isTntDispenserItem(ItemStack item) {
        if (item == null || item.getType() != Material.DISPENSER) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
            .has(ForgeCommand.TNT_DISPENSER_KEY, PersistentDataType.BYTE);
    }
}
