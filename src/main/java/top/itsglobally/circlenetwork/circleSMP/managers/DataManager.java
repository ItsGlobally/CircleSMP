package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.itsglobally.circlenetwork.circleSMP.utils.ManagerRegistry;
import top.nontage.nontagelib.annotations.YamlIgnore;
import top.nontage.nontagelib.config.BaseConfig;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager extends Manager {

    private static File configDir;
    private static MainConfig mainConfig;
    private static PlayerDatas playerDatas;
    private static Claims claims;

    public DataManager() {
        configDir = plugin.getDataFolder();
        if (!configDir.exists()) configDir.mkdirs();

        mainConfig = register(new MainConfig(), "config");
        playerDatas = register(new PlayerDatas(), "playerdata");
        claims = register(new Claims(), "claims");
        reload();
    }

    public void reload() {
        mainConfig.reload();
        playerDatas.reload();
        claims.reload();
        if (playerDatas.data == null)
            playerDatas.data = new LinkedHashMap<>();
        playerDatas.loadCache();
        claims.loadClaims();

    }

    public Claims getClaims() {
        return claims;
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
        Map<String, Object> cperms = new LinkedHashMap<>();
        for (Map.Entry<UUID, Set<DataManager.ClaimPerms>> entry : c.getColabs().entrySet()) {
            UUID uuid = entry.getKey();
            Set<DataManager.ClaimPerms> perms = entry.getValue();
            List<String> permNames = perms.stream()
                    .map(Enum::name)
                    .toList();
            cperms.put(uuid.toString(), permNames);
        }

        map.put("colabs", cperms);
        map.put("owner", c.getOwner().toString());
        map.put("name", c.getName());

        map.put("coveredChunks", new ArrayList<>(c.getCoveredChunks()));
        map.put("id", c.getId().toString());

        return map;
    }


    @SuppressWarnings("unchecked")
    private Claim deserializeClaim(Map<String, Object> map) {
        if (map == null) return null;

        UUID id = UUID.fromString((String) map.get("id"));
        UUID owner = UUID.fromString((String) map.get("owner"));
        String name = (String) map.get("name");
        Claim claim = new Claim(name, owner, id);
        List<Long> chunks = (List<Long>) map.get("coveredChunks");
        if (chunks != null) claim.getCoveredChunks().addAll(chunks);

        Map<String, Object> cperms = (Map<String, Object>) map.get("colabs");
        if (cperms != null) {
            for (Map.Entry<String, Object> entry : cperms.entrySet()) {
                UUID colabUuid = UUID.fromString(entry.getKey());
                List<String> permNames = (List<String>) entry.getValue();
                Set<DataManager.ClaimPerms> perms = permNames.stream()
                        .map(DataManager.ClaimPerms::valueOf)
                        .collect(Collectors.toSet());
                claim.getColabs().put(colabUuid, perms);
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

        private final List<Map<String, Object>> rawClaims = new ArrayList<>();

        @YamlIgnore
        private final Map<UUID, List<Claim>> cache = new HashMap<>();

        public void loadClaims() {
            cache.clear();
            for (Map<String, Object> map : rawClaims) {
                Claim claim = deserializeClaim(map);
                if (claim != null) {
                    cache.computeIfAbsent(claim.getOwner(), k -> new ArrayList<>()).add(claim);
                }
            }
        }

        public List<Claim> getAllClaims() {
            return cache.values().stream().flatMap(List::stream).toList();
        }

        public List<Claim> getClaims(UUID owner) {
            return cache.getOrDefault(owner, Collections.emptyList());
        }

        public Claim getClaimByChunk(int chunkX, int chunkZ) {
            long key = ((long) chunkX << 32) | (chunkZ & 0xffffffffL);
            for (List<Claim> list : cache.values()) {
                for (Claim c : list) {
                    if (c.getCoveredChunks().contains(key)) {
                        return c;
                    }
                }
            }
            return null;
        }

        public void addClaim(Claim claim) {
            if (claim == null || claim.getOwner() == null) return;

            cache.computeIfAbsent(claim.getOwner(), k -> new ArrayList<>()).add(claim);

            // 移除舊的相同 id（防止重複）
            rawClaims.removeIf(m -> Objects.equals(m.get("id"), claim.getId().toString()));

            rawClaims.add(serializeClaim(claim));
            save();
        }

        public void removeClaim(Claim claim) {
            if (claim == null || claim.getOwner() == null) return;

            List<Claim> list = cache.get(claim.getOwner());
            if (list != null) {
                list.remove(claim);
                if (list.isEmpty()) {
                    cache.remove(claim.getOwner());
                }
            }

            rawClaims.removeIf(m -> Objects.equals(m.get("id"), claim.getId().toString()));
            save();
        }

        public void removeAllClaims(UUID owner) {
            if (owner == null) return;

            cache.remove(owner);
            rawClaims.removeIf(m -> Objects.equals(m.get("owner"), owner.toString()));
            save();
        }

        public void flush() {
            rawClaims.clear();
            for (List<Claim> list : cache.values()) {
                for (Claim c : list) {
                    rawClaims.add(serializeClaim(c));
                }
            }
            save();
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
