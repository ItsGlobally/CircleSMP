package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.nontage.nontagelib.annotations.YamlIgnore;
import top.nontage.nontagelib.config.BaseConfig;

import java.io.File;
import java.util.*;

public class DataManager extends Manager {

    private static File configDir;
    private static MainConfig mainConfig;
    private static PlayerDatas playerDatas;

    public DataManager() {
        configDir = plugin.getDataFolder();
        if (!configDir.exists()) configDir.mkdirs();

        mainConfig = register(new MainConfig(), "config");
        playerDatas = register(new PlayerDatas(), "playerdata");

        reload();
    }

    public void reload() {
        mainConfig.reload();
        playerDatas.reload();
        if (playerDatas.data == null)
            playerDatas.data = new LinkedHashMap<>();
        playerDatas.loadCache();
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public PlayerDatas getPlayerDatas() {
        return playerDatas;
    }

    private <T extends BaseConfig> T register(T config, String name) {
        File file = new File(configDir, name + ".yml");
        config.initFile(file);
        config.reload();
        return config;
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

    private Map<String, Object> serializeClaim(Claim c) {
        if (c == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        List<String> colabs = c.getColabs().stream()
                .map(UUID::toString)
                .toList();
        map.put("colabs", colabs); //set<uuid>
        map.put("owner", c.getOwner());
        List<Long> coveredChunks = new ArrayList<>(c.getCoveredChunks());
        map.put("coveredChunks", coveredChunks); //set<long>
        map.put("name", c.getName());
        return map;
    }

    private Claim deserializeClaim(Map<String, Object> map) {
        if (map == null) return null;

        String name = (String) map.get("name");
        UUID owner = UUID.fromString((String) map.get("owner"));
        Claim claim = new Claim(name, owner);

        List<String> colabList = (List<String>) map.get("colabs");
        if (colabList != null) {
            for (String s : colabList) {
                claim.addColab(UUID.fromString(s));
            }
        }

        List<Long> chunkList = (List<Long>) map.get("coveredChunks");
        if (chunkList != null) {
            for (Long key : chunkList) {
                claim.addChunk(key);
            }
        }

        return claim;
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

    public static class MainConfig extends BaseConfig {
        public int tpaTimeoutSecond = 60;
        public int maxHomes = 5;

        public int getTpaTimeoutSecond() {
            return tpaTimeoutSecond;
        }

        public int getMaxHomes() {
            return maxHomes;
        }
    }

    public class Claims extends BaseConfig {

        private final Set<Map<String, Object>> rawClaims = new LinkedHashSet<>();

        @YamlIgnore
        private final Map<UUID, Claim> cache = new HashMap<>();

        public void loadClaims() {
            cache.clear();
            for (Map<String, Object> map : rawClaims) {
                Claim claim = deserializeClaim(map);
                if (claim != null) {
                    cache.put(claim.getOwner(), claim);
                }
            }
        }

        public Set<Claim> getClaims() {
            return new HashSet<>(cache.values());
        }

        public Claim getClaim(UUID owner) {
            return cache.get(owner);
        }

        public void addClaim(Claim claim) {
            cache.put(claim.getOwner(), claim);
            rawClaims.add(serializeClaim(claim));
            save();
        }

        public void removeClaim(UUID owner) {
            Claim claim = cache.remove(owner);
            if (claim != null) {
                rawClaims.removeIf(m -> ((String) m.get("owner")).equals(owner.toString()));
                save();
            }
        }
    }

    public enum ClaimPerms {
        PLACE, BREAK, INTERACT, MOVEINTO
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
