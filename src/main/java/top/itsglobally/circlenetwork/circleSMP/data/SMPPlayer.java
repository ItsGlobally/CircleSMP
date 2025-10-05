package top.itsglobally.circlenetwork.circleSMP.data;

import org.bukkit.entity.Player;

import java.util.UUID;

public class SMPPlayer {
    private final String name;
    private final UUID uuid;
    private final Player player;
    public SMPPlayer(Player p) {
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        this.player = p;

    }
}
