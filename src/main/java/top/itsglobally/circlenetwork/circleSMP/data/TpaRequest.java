package top.itsglobally.circlenetwork.circleSMP.data;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TpaRequest {
    private final Player sender;
    private final Player target;
    private BukkitTask task;
    private TpaType type;
    public TpaRequest(Player sender, Player target, BukkitTask task, TpaType type) {
        this.sender = sender;
        this.target = target;
        this.task = task;
        this.type = type;
    }

    public Player getSender() {
        return sender;
    }

    public BukkitTask getTask() {
        return task;
    }

    public Player getTarget() {
        return target;
    }

    public TpaType getType() {
        return type;
    }
}
