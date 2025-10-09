package top.itsglobally.circlenetwork.circleSMP.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.itsglobally.circlenetwork.circleSMP.managers.ClaimManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;

public class ClaimListener implements IListener, Listener {
    ClaimManager cm = ManagerRegistry.get(ClaimManager.class);
    @EventHandler
    public void bbreak(BlockBreakEvent e) {
        Claim c = cm.getClaimAt(e.getBlock().getLocation());
        if (c != null) {
            if (e.getPlayer().getUniqueId() != c.getOwner() || !c.isInColab(e.getPlayer().getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void bplace(BlockPlaceEvent e) {
        Claim c = cm.getClaimAt(e.getBlock().getLocation());
        if (c != null) {
            if (e.getPlayer().getUniqueId() != c.getOwner() || !c.isInColab(e.getPlayer().getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
}
