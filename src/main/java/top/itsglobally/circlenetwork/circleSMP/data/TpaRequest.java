package top.itsglobally.circlenetwork.circleSMP.data;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TpaRequest {
    private final Player sender;
    private final Player target;
    private BukkitTask task;
    public TpaRequest(Player sender, Player target, BukkitTask task) {
        this.sender = sender;
        this.target = target;
        this.task = task;
    }
}
