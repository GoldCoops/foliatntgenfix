package net.goldcoops.foliatntgenfix;

import io.papermc.paper.event.block.BlockFailedDispenseEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import static net.goldcoops.foliatntgenfix.Foliatntgenfix.*;

public class DispenserListener implements Listener {

    private final JavaPlugin plugin;

    private final Foliatntgenfix main;



    public DispenserListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.main = Foliatntgenfix.getInstance();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!main.isTntDispenserItem(item) ) return;
        if (!(event.getBlock().getState() instanceof Dispenser dispenser)) return;
        dispenser.getPersistentDataContainer().set(Foliatntgenfix.TNT_DISPENSER_KEY, PersistentDataType.BYTE, main.getTntDispenserItemLevel(item));
        dispenser.update();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = main.toBlockLocation(event.getBlock().getLocation());
        if (!main.isTntDispenserBlock(event.getBlock())) return;

        event.setDropItems(false);
        plugin.getLogger().info(String.valueOf(main.getTntDispenserBlockLevel(event.getBlock())));
        ItemStack tntDispenser = main.createTntDispenserItem(main.getTntDispenserBlockLevel(event.getBlock()));
        loc.getWorld().dropItemNaturally(loc, tntDispenser);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(main::isTntDispenserBlock);
    }


    @EventHandler
    public void onFailedDispenser(BlockFailedDispenseEvent event) {
        main.isTntDispenserBlock(event.getBlock());
         Block block = event.getBlock();
        if (block.getType() != Material.DISPENSER) return;
        Location loc = main.toBlockLocation(block.getLocation());
        if (!main.isTntDispenserBlock(block)) return;
        plugin.getServer().getRegionScheduler().execute(plugin, loc, () -> {fireTnt(loc);});
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
}
