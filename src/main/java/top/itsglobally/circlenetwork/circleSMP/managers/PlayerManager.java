package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.itsglobally.circlenetwork.circleSMP.data.SMPPlayer;
import top.itsglobally.circlenetwork.circleSMP.utils.ConfigRegister;
import top.nontage.nontagelib.annotations.AutoListener;
import top.nontage.nontagelib.annotations.YamlIgnore;
import top.nontage.nontagelib.config.BaseConfig;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static top.itsglobally.circlenetwork.circleSMP.utils.serializer.deserializeLocation;

@AutoListener
public class PlayerManager extends Manager {

    private final Map<UUID, SMPPlayer> players;
    private static PlayerDatas pds;

    public PlayerManager() {
        this.players = new HashMap<>();
        pds = ConfigRegister.register(new PlayerDatas(), "playerdata");
        pds.reload();
    }

    public PlayerDatas getPlayerDatas() {
        return pds;
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
    private Map<String, Object> serializePlayerData(PlayerData data) {
        if (data == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", data.getUuid().toString());

        Map<String, Object> homes = new LinkedHashMap<>();
        for (Map.Entry<String, Location> entry : data.getHomes().entrySet()) {
            homes.put(entry.getKey(), serializeLocation(entry.getValue()));
        }
        map.put("homes", homes);
        return map;
    }

    private PlayerData deserializePlayerData(Map<String, Object> map) {
        if (map == null) return null;

        Object rawUuid = map.get("uuid");
        UUID uuid = (rawUuid instanceof UUID)
                ? (UUID) rawUuid
                : UUID.fromString(String.valueOf(rawUuid));

        PlayerData pd = new PlayerData(uuid);

        Object homesObj = map.get("homes");
        if (homesObj instanceof Map<?, ?> homesMap) {
            for (Map.Entry<?, ?> entry : homesMap.entrySet()) {
                String name = entry.getKey().toString();
                if (entry.getValue() instanceof Map<?, ?> locMap) {
                    pd.setHome(name, deserializeLocation((Map<String, Object>) locMap));
                }
            }
        }
        return pd;
    }

    private Map<String, Object> serializeLocation(Location loc) {
        if (loc == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("x", loc.getX());
        map.put("y", loc.getY());
        map.put("z", loc.getZ());
        map.put("yaw", loc.getYaw());
        map.put("pitch", loc.getPitch());
        return map;
    }

    private Location deserializeLocation(Map<String, Object> map) {
        if (map == null) return null;
        return new Location(
                Bukkit.getWorld((String) map.get("world")),
                ((Number) map.get("x")).doubleValue(),
                ((Number) map.get("y")).doubleValue(),
                ((Number) map.get("z")).doubleValue(),
                ((Number) map.get("yaw")).floatValue(),
                ((Number) map.get("pitch")).floatValue()
        );
    }
    public static class PlayerData {
        private final UUID uuid;
        private final Map<String, Location> homes = new LinkedHashMap<>();

        public PlayerData(UUID uuid) {
            this.uuid = uuid;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Map<String, Location> getHomes() {
            return homes;
        }

        public void setHome(String name, Location loc) {
            homes.put(name.toLowerCase(), loc);
        }

        public Location getHome(String name) {
            return homes.get(name.toLowerCase());
        }

        public void removeHome(String name) {
            homes.remove(name.toLowerCase());
        }

        public Set<String> listHomes() {
            return homes.keySet();
        }
    }

    public class PlayerDatas extends BaseConfig {
        public Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        @YamlIgnore
        private final Map<UUID, PlayerData> cache = new HashMap<>();

        public void loadCache() {
            cache.clear();
            for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                UUID uuid = UUID.fromString(entry.getKey());
                cache.put(uuid, deserializePlayerData(entry.getValue()));
            }
        }

        public PlayerData get(Player player) {
            return get(player.getUniqueId());
        }

        public PlayerData get(UUID uuid) {
            if (cache.containsKey(uuid))
                return cache.get(uuid);

            Map<String, Object> map = data.get(uuid.toString());
            PlayerData pd = (map == null)
                    ? new PlayerData(uuid)
                    : deserializePlayerData(map);

            cache.put(uuid, pd);
            data.put(uuid.toString(), serializePlayerData(pd));
            save();
            return pd;
        }

        public void update(PlayerData playerData) {
            cache.put(playerData.getUuid(), playerData);
            data.put(playerData.getUuid().toString(), serializePlayerData(playerData));
            save();
        }

        public void flush() {
            for (PlayerData pd : cache.values()) {
                data.put(pd.getUuid().toString(), serializePlayerData(pd));
            }
            save();
        }
    }
}
