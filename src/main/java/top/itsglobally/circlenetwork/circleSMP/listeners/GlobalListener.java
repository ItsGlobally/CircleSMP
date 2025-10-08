package top.itsglobally.circlenetwork.circleSMP.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.itsglobally.circlenetwork.circleSMP.managers.PlayerManager;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.nontage.nontagelib.annotations.AutoListener;

@AutoListener
public class GlobalListener implements Listener {
    @EventHandler
    public void join(PlayerJoinEvent e) {
        ManagerRegistry.get(PlayerManager.class).addPlayer(e.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        ManagerRegistry.get(PlayerManager.class).removePlayer(e.getPlayer());
    }
}
