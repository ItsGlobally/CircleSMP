package top.itsglobally.circlenetwork.circleSMP.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.itsglobally.circlenetwork.circleSMP.managers.ClaimManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.itsglobally.circlenetwork.circleSMP.utils.MessageUtil;

public class ClaimListener implements IListener, Listener {
    ClaimManager cm = ManagerRegistry.get(ClaimManager.class);
    @EventHandler
    public void bbreak(BlockBreakEvent e) {
        Claim c = cm.getClaimAt(e.getBlock().getLocation());
        if (c != null) {
            if (e.getPlayer().getUniqueId() != c.getOwner() || !c.isInColab(e.getPlayer().getUniqueId())) {
                e.setCancelled(true);
                MessageUtil.sendActionBar(e.getPlayer(), "&7You cannot break block in a area which has been claimed!");
            }
        }
    }
    @EventHandler
    public void bplace(BlockPlaceEvent e) {
        Claim c = cm.getClaimAt(e.getBlock().getLocation());
        if (c != null) {
            if (e.getPlayer().getUniqueId() != c.getOwner() || !c.isInColab(e.getPlayer().getUniqueId())) {
                e.setCancelled(true);
                MessageUtil.sendActionBar(e.getPlayer(), "&7You cannot place block in a area which has been claimed!");
            }
        }
    }
    @EventHandler
    public void binteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            assert e.getClickedBlock() != null;
            Claim c = cm.getClaimAt(e.getClickedBlock().getLocation());
            if (c != null) {
                if (e.getPlayer().getUniqueId() != c.getOwner() || !c.isInColab(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);
                    MessageUtil.sendActionBar(e.getPlayer(), "&7You cannot interact to block in a area which has been claimed!");
                }
            }
        }
    }
    @EventHandler
    public void pmove(PlayerMoveEvent e) {
        Claim c1 = cm.getClaimAt(e.getFrom());
        Claim c2 = cm.getClaimAt(e.getTo());
        if (!c1.equals(c2)) {
            MessageUtil.sendDebugActionBar(e.getPlayer(), "claim changed");
        }
    }
}
