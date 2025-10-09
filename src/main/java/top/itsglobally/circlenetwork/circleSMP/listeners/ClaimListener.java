package top.itsglobally.circlenetwork.circleSMP.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.itsglobally.circlenetwork.circleSMP.managers.ClaimManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;

public class ClaimListener implements IListener, Listener {
    ClaimManager cm = ManagerRegistry.get(ClaimManager.class);
    @EventHandler
    public void move(PlayerMoveEvent e) {
        Claim c = cm.getClaimAt(e.getTo());
        if (c != null) {
            if (e.getPlayer().getUniqueId() != c.getOwner()) {
                e.setCancelled(true);
            }
        }
    }
}
