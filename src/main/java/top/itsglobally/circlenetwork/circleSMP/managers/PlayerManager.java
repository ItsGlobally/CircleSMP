package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.data.SMPPlayer;
import top.itsglobally.circlenetwork.circleSMP.data.TpaRequest;

import java.util.*;

public class PlayerManager extends Manager {

    private HashMap<UUID, Set<TpaRequest>> tpaRequests;

    public PlayerManager() {
        tpaRequests = new HashMap<>();
    }

    public Set<TpaRequest> getTpaRequests(Player p) {
        return tpaRequests.get(p.getUniqueId());
    }
    public TpaRequest getTpaRequest(Player p, Player target) {
        return tpaRequests.get(p.getUniqueId()).stream()
                .filter(req -> req.getSender().equals(p) && req.getTarget().equals(target))
                .findFirst()
                .orElse(null);
    }
    public void addTpaRequest(Player p, TpaRequest t) {
        Set<TpaRequest> s = getTpaRequests(p);
        s.add(t);
        tpaRequests.put(p.getUniqueId(), s);
    }
    public void removeTpaRequest(Player p, TpaRequest t) {
        Set<TpaRequest> s = getTpaRequests(p);
        t.getTask().cancel();
        s.remove(t);
        tpaRequests.put(p.getUniqueId(), s);
    }

}
