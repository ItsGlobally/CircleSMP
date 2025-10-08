package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.data.SMPPlayer;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AutoListener
public class PlayerManager extends Manager {

    private final Map<UUID, SMPPlayer> players;

    public PlayerManager() {
        this.players = new HashMap<>();
    }

    public Map<UUID, SMPPlayer> getPlayers() {
        return players;
    }

    public SMPPlayer getPlayer(UUID u) {
        return players.get(u);
    }

    public SMPPlayer getPlayer(Player p) {
        return players.get(p.getUniqueId());
    }

    public void addPlayer(Player p) {
        players.put(p.getUniqueId(), new SMPPlayer(p));
    }

    public void removePlayer(Player p) {
        players.remove(p.getUniqueId());
    }

}
