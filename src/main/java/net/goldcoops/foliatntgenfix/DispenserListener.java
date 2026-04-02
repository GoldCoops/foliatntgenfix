package net.goldcoops.foliatntgenfix;

import io.papermc.paper.event.block.BlockFailedDispenseEvent;
import net.goldcoops.foliatntgenfix.commands.ForgeCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

import static net.goldcoops.foliatntgenfix.Foliatntgenfix.toBlockLocation;

public class DispenserListener implements Listener {

    private final JavaPlugin plugin;
    private final DispenserStorage storage;
    private final Set<Location> placedDispenserLocations;


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
        placedDispenserLocations.add(toBlockLocation(event.getBlockPlaced().getLocation()));
        storage.save(placedDispenserLocations);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = toBlockLocation(event.getBlock().getLocation());
        if (!placedDispenserLocations.remove(loc)) return;
        storage.save(placedDispenserLocations);
        event.setDropItems(false);
        ItemStack tntDispenser = Foliatntgenfix.createTntDispenserItem();
        loc.getWorld().dropItemNaturally(loc, tntDispenser);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            if (block.getType() != Material.DISPENSER) return false;
            return placedDispenserLocations.contains(toBlockLocation(block.getLocation()));
        });
    }


    @EventHandler
    public void onFailedDispenser(BlockFailedDispenseEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.DISPENSER) return;
        Location loc = toBlockLocation(block.getLocation());
        if (!placedDispenserLocations.contains(loc)) return;
        plugin.getServer().getRegionScheduler().execute(plugin, loc, () -> {fireTnt(loc);});
    }

    public void saveAll() {
        storage.save(placedDispenserLocations);
    }


    private void fireTnt(Location dispenserLoc) {
        if (dispenserLoc.getWorld() == null) return;
        Directional blockFace = (Directional) dispenserLoc.getBlock().getBlockData();
        BlockFace face = blockFace.getFacing();
        Location spawnLoc = dispenserLoc.clone();
        spawnLoc.add(0.5, 0, 0.5);
        switch (face) {
            case NORTH:
                spawnLoc.add(0, 0, -1);
                break;
            case SOUTH:
                spawnLoc.add(0, 0, 1);
                break;
            case WEST:
                spawnLoc.add(-1, 0, 0);
                break;
            case EAST:
                spawnLoc.add(1, 0, 0);
                break;
            case UP:
                spawnLoc.add(0, 1, 0);
                break;
            case DOWN:
                spawnLoc.add(0, -1, 0);
        }
        dispenserLoc.getWorld().spawnEntity(spawnLoc, EntityType.TNT);
    }

    private boolean isTntDispenserItem(ItemStack item) {
        if (item == null || item.getType() != Material.DISPENSER) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
            .has(ForgeCommand.TNT_DISPENSER_KEY, PersistentDataType.BYTE);
    }




}
