package top.itsglobally.circlenetwork.circleSMP.data;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class TpaRequest {
    private final Player sender;
    private final Player target;
    private final BukkitTask task;
    private final TpaType type;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TpaRequest that)) return false;
        return sender.equals(that.sender) && target.equals(that.target) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, target, type);
    }
}
