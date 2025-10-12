package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.Location;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.itsglobally.circlenetwork.circleSMP.managers.Manager;
import top.itsglobally.circlenetwork.circleSMP.utils.ConfigRegister;
import top.nontage.nontagelib.annotations.YamlIgnore;
import top.nontage.nontagelib.config.BaseConfig;

import java.util.*;
import java.util.stream.Collectors;

public class ClaimManager extends Manager {

    private final Map<Long, List<Claim>> chunkClaims = new HashMap<>();
    private final Map<UUID, Claim> playerCurrentClaim = new HashMap<>();
    private final Map<UUID, Set<Claim>> allClaims = new HashMap<>();
    private static Claims claims;
    public ClaimManager() {
        claims = ConfigRegister.register(new Claims(), "config");
    }

    public Claims getClaims() {
        return claims;
    }

    public void registerClaim(Claim claim) {
        if (claim == null || claim.getOwner() == null) return;

        allClaims.computeIfAbsent(claim.getOwner(), k -> new HashSet<>()).add(claim);

        for (long chunkKey : claim.getCoveredChunks()) {
            chunkClaims.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(claim);
        }
    }
    public Claim getClaimAt(Location loc) {
        long key = (((long) loc.getBlockX() >> 4) << 32) | ((loc.getBlockZ() >> 4) & 0xffffffffL);
        List<Claim> claims = chunkClaims.get(key);
        if (claims != null && !claims.isEmpty()) {
            return claims.getFirst();
        }
        return null;
    }
    public Claim getPlayerClaim(UUID uuid) {
        return playerCurrentClaim.get(uuid);
    }

    public void setPlayerClaim(UUID uuid, Claim claim) {
        if (uuid == null) return;
        if (claim == null) {
            playerCurrentClaim.remove(uuid);
        } else {
            playerCurrentClaim.put(uuid, claim);
        }
    }
    public Collection<Claim> getAllClaims() {
        Set<Claim> all = new HashSet<>();
        for (Set<Claim> set : allClaims.values()) {
            all.addAll(set);
        }
        return all;
    }

    public Set<Claim> getClaims(UUID u) {
        return allClaims.getOrDefault(u, Collections.emptySet());
    }

    public void unregisterClaim(Claim claim) {
        if (claim == null || claim.getOwner() == null) return;

        Set<Claim> playerClaims = allClaims.get(claim.getOwner());
        if (playerClaims != null) {
            playerClaims.remove(claim);
            if (playerClaims.isEmpty()) {
                allClaims.remove(claim.getOwner());
            }
        }

        for (long chunkKey : claim.getCoveredChunks()) {
            List<Claim> list = chunkClaims.get(chunkKey);
            if (list != null) {
                list.remove(claim);
                if (list.isEmpty()) {
                    chunkClaims.remove(chunkKey);
                }
            }
        }
    }

    private Map<String, Object> serializeClaim(Claim c) {
        if (c == null) return null;

        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> cperms = new LinkedHashMap<>();
        for (Map.Entry<UUID, Set<ClaimPerms>> entry : c.getColabs().entrySet()) {
            UUID uuid = entry.getKey();
            Set<ClaimPerms> perms = entry.getValue();
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
                Set<ClaimPerms> perms = permNames.stream()
                        .map(ClaimPerms::valueOf)
                        .collect(Collectors.toSet());
                claim.getColabs().put(colabUuid, perms);
            }
        }

        return claim;
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

}
