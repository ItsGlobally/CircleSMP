package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.Location;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
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
        claims = ConfigRegister.register(new Claims(), "claim");
        claims.reload();
        claims.loadClaims();
        claims.loadClaims();
        claims.loadClaims();
        for (Claim claim : claims.getAllClaims()) {
            allClaims.computeIfAbsent(claim.getOwner(), k -> new HashSet<>()).add(claim);
            for (Long chunk : claim.getCoveredChunks()) {
                chunkClaims.computeIfAbsent(chunk, k -> new ArrayList<>()).add(claim);
            }
        }
    }

    public Claims getClaims() {
        return claims;
    }

    public void registerClaim(Claim claim) {
        if (claim == null || claim.getOwner() == null) return;

        allClaims.computeIfAbsent(claim.getOwner(), k -> new HashSet<>()).add(claim);
        claim.getCoveredChunks().forEach(chunk ->
                chunkClaims.computeIfAbsent(chunk, k -> new ArrayList<>()).add(claim));

        claims.addClaim(claim);
    }

    public void unregisterClaim(Claim claim) {
        if (claim == null || claim.getOwner() == null) return;

        Optional.ofNullable(allClaims.get(claim.getOwner()))
                .ifPresent(set -> {
                    set.remove(claim);
                    if (set.isEmpty()) allClaims.remove(claim.getOwner());
                });

        claim.getCoveredChunks().forEach(chunk -> {
            List<Claim> list = chunkClaims.get(chunk);
            if (list != null && list.remove(claim) && list.isEmpty()) chunkClaims.remove(chunk);
        });

        claims.removeClaim(claim);
    }

    public Claim getClaimAt(Location loc) {
        long key = (((long) loc.getBlockX() >> 4) << 32) | ((loc.getBlockZ() >> 4) & 0xffffffffL);
        List<Claim> claims = chunkClaims.get(key);
        return (claims == null || claims.isEmpty()) ? null : claims.getFirst();
    }

    public Claim getPlayerClaim(UUID uuid) {
        return playerCurrentClaim.get(uuid);
    }

    public void setPlayerClaim(UUID uuid, Claim claim) {
        if (uuid == null) return;
        if (claim == null) playerCurrentClaim.remove(uuid);
        else playerCurrentClaim.put(uuid, claim);
    }

    public Collection<Claim> getAllClaims() {
        return allClaims.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    public Set<Claim> getClaims(UUID u) {
        return allClaims.getOrDefault(u, Collections.emptySet());
    }

    private Map<String, Object> serializeClaim(Claim c) {
        if (c == null) return null;

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId().toString());
        map.put("owner", c.getOwner().toString());
        map.put("name", c.getName());
        map.put("coveredChunks", new ArrayList<>(c.getCoveredChunks()));

        Map<String, Object> cperms = c.getColabs().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().stream().map(Enum::name).toList(),
                        (a, b) -> b, LinkedHashMap::new));
        map.put("colabs", cperms);

        return map;
    }

    @SuppressWarnings("unchecked")
    private Claim deserializeClaim(Map<String, Object> map) {
        if (map == null) return null;

        UUID id = UUID.fromString((String) map.get("id"));
        UUID owner = UUID.fromString((String) map.get("owner"));
        Claim claim = new Claim((String) map.get("name"), owner, id);

        Optional.ofNullable((List<Long>) map.get("coveredChunks"))
                .ifPresent(claim.getCoveredChunks()::addAll);

        Map<String, Object> cperms = (Map<String, Object>) map.get("colabs");
        if (cperms != null) {
            cperms.forEach((key, value) -> {
                UUID colabUuid = UUID.fromString(key);
                Set<ClaimPerms> perms = ((List<String>) value).stream()
                        .map(ClaimPerms::valueOf)
                        .collect(Collectors.toSet());
                claim.getColabs().put(colabUuid, perms);
            });
        }

        return claim;
    }

    public class Claims extends BaseConfig {

        private final List<Map<String, Object>> rawClaims = new ArrayList<>();

        @YamlIgnore
        private final Map<UUID, List<Claim>> cache = new HashMap<>();

        public void loadClaims() {
            cache.clear();
            rawClaims.stream()
                    .map(ClaimManager.this::deserializeClaim)
                    .filter(Objects::nonNull)
                    .forEach(claim -> cache.computeIfAbsent(claim.getOwner(), k -> new ArrayList<>()).add(claim));
        }

        public List<Claim> getAllClaims() {
            return cache.values().stream().flatMap(List::stream).toList();
        }

        public List<Claim> getClaims(UUID owner) {
            return cache.getOrDefault(owner, Collections.emptyList());
        }

        public Claim getClaimByChunk(int chunkX, int chunkZ) {
            long key = ((long) chunkX << 32) | (chunkZ & 0xffffffffL);
            return cache.values().stream()
                    .flatMap(List::stream)
                    .filter(c -> c.getCoveredChunks().contains(key))
                    .findFirst()
                    .orElse(null);
        }

        public void addClaim(Claim claim) {
            if (claim == null || claim.getOwner() == null) return;
            cache.computeIfAbsent(claim.getOwner(), k -> new ArrayList<>()).add(claim);
            rawClaims.removeIf(m -> Objects.equals(m.get("id"), claim.getId().toString()));
            rawClaims.add(serializeClaim(claim));
            save();
        }

        public void removeClaim(Claim claim) {
            if (claim == null || claim.getOwner() == null) return;
            Optional.ofNullable(cache.get(claim.getOwner()))
                    .ifPresent(list -> {
                        list.remove(claim);
                        if (list.isEmpty()) cache.remove(claim.getOwner());
                    });
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
            cache.values().stream()
                    .flatMap(List::stream)
                    .map(ClaimManager.this::serializeClaim)
                    .forEach(rawClaims::add);
            save();
        }
    }

    public enum ClaimPerms {
        PLACE, BREAK, INTERACT, MOVEINTO
    }
}
