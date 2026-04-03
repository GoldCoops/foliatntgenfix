package net.goldcoops.foliatntgenfix;

import io.papermc.paper.event.block.BlockFailedDispenseEvent;
import net.goldcoops.foliatntgenfix.commands.ForgeCommand;
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


import static net.goldcoops.foliatntgenfix.Foliatntgenfix.toBlockLocation;

public class DispenserListener implements Listener {

    private final JavaPlugin plugin;



    public DispenserListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!isTntDispenserItem(item) ) return;
        if (!(event.getBlock().getState() instanceof Dispenser dispenser)) return;
        dispenser.getPersistentDataContainer().set(ForgeCommand.TNT_DISPENSER_KEY, PersistentDataType.BYTE, (byte) 1);
        dispenser.update();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = toBlockLocation(event.getBlock().getLocation());
        if (!isTntDispenserBlock(event.getBlock())) return;

        event.setDropItems(false);
        ItemStack tntDispenser = Foliatntgenfix.createTntDispenserItem(1);
        loc.getWorld().dropItemNaturally(loc, tntDispenser);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isTntDispenserBlock);
    }


    @EventHandler
    public void onFailedDispenser(BlockFailedDispenseEvent event) {
        isTntDispenserBlock(event.getBlock());
        Block block = event.getBlock();
        if (block.getType() != Material.DISPENSER) return;
        Location loc = toBlockLocation(block.getLocation());
        if (!isTntDispenserBlock(block)) return;
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

    private boolean isTntDispenserItem(ItemStack item) {
        if (item == null || item.getType() != Material.DISPENSER) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
            .has(ForgeCommand.TNT_DISPENSER_KEY, PersistentDataType.BYTE);
    }

    private boolean isTntDispenserBlock(Block block) {
        if (block.getType() != Material.DISPENSER) return false;
        if (!(block.getState() instanceof Dispenser dispenser)) return false;
        if (dispenser.getPersistentDataContainer().isEmpty()) return false;
        return dispenser.getPersistentDataContainer().has(ForgeCommand.TNT_DISPENSER_KEY, PersistentDataType.BYTE);
    }




}
