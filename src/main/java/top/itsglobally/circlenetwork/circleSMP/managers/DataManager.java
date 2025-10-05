package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
    public class MainConfig extends BaseConfig {
        public int tpaTimeoutSecond = 60;
        public int maxHomes;
        public int getTpaTimeoutSecond() {
            return tpaTimeoutSecond;
        }

        public int getMaxHomes() {
            return maxHomes;
        }
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
        return new PlayerData((UUID) map.get("uuid"));
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

    public class PlayerDatas extends BaseConfig {
        public Map<UUID, Map<String, Object>> data = new LinkedHashMap<>();

        public PlayerData get(Player player) {
            return get(player.getUniqueId());
        }

        public PlayerData get(UUID uuid) {
            Map<String, Object> map = data.get(uuid);
            if (map == null) {
                PlayerData pd = new PlayerData(uuid);
                data.put(uuid, serializePlayerData(pd));
                save();
                return pd;
            }
            return deserializePlayerData(map);
        }
    }
    public static class PlayerData {
        private final UUID uuid;
        private Map<String, Location> homes = new LinkedHashMap<>();

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
}
