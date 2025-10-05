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
        return tpaRequests.getOrDefault(p.getUniqueId(), Collections.emptySet()).stream()
                .filter(req -> req.getSender().equals(p) && req.getTarget().equals(target))
                .findFirst()
                .orElse(null);

    }
    public void addTpaRequest(Player p, TpaRequest t) {
        Set<TpaRequest> s = tpaRequests.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
        s.add(t);
    }

    public void removeTpaRequest(Player sender, TpaRequest t) {
        Set<TpaRequest> s = tpaRequests.get(sender.getUniqueId());
        if (s != null) {
            t.getTask().cancel();
            s.remove(t);
            if (s.isEmpty()) tpaRequests.remove(sender.getUniqueId());
        }
    }



}
