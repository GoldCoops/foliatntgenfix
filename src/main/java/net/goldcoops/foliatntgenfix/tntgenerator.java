package net.goldcoops.foliatntgenfix;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import static net.goldcoops.foliatntgenfix.Foliatntgenfix.getInstance;

import static org.bukkit.Bukkit.getServer;

public class tntgenerator {
    RegionScheduler scheduler = getServer().getRegionScheduler();


    public ScheduledTask createTntGen(Location spawnLocation) {
        return scheduler.runAtFixedRate(getInstance(),
                spawnLocation,
                task -> {
                    spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.TNT);
                },
                1L,
                10L
        );
    }
}
