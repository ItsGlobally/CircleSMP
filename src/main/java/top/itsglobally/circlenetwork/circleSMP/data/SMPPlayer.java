package top.itsglobally.circlenetwork.circleSMP.data;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SMPPlayer {
    private final String name;
    private final UUID uuid;
    private final Player player;
    private final Set<TpaRequest> tpaRequests;

    public SMPPlayer(Player p) {
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        this.player = p;
        tpaRequests = new HashSet<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }

    public Set<TpaRequest> getTpaRequests() {
        return tpaRequests;
    }

    public void addTpaRequest(TpaRequest t) {
        tpaRequests.add(t);
    }

    public void removeTpaRequest(TpaRequest t) {
        t.getTask().cancel();
        tpaRequests.remove(t);
    }

    public TpaRequest getTpaRequest(Player target) {
        return tpaRequests.stream()
                .filter(req -> req.getSender().equals(player) && req.getTarget().equals(target))
                .findFirst()
                .orElse(null);
    }
}
