package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.Location;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;
import top.itsglobally.circlenetwork.circleSMP.managers.Manager;

import java.util.*;

public class ClaimManager extends Manager {

    private final Map<Long, List<Claim>> chunkClaims = new HashMap<>();
    private final Map<UUID, Claim> playerCurrentClaim = new HashMap<>();
    private final Map<UUID, Set<Claim>> allClaims = new HashMap<>();

    /**
     * 註冊一個 Claim 到所有快取中
     */
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
}
