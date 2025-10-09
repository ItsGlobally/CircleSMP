package top.itsglobally.circlenetwork.circleSMP.managers;

import org.bukkit.Location;
import top.itsglobally.circlenetwork.circleSMP.data.Claim;

import java.util.*;

public class ClaimManager extends Manager {

    private final Map<Long, List<Claim>> chunkClaims = new HashMap<>();
    private final Map<UUID, Claim> playerCurrentClaim = new HashMap<>();
    private final Map<UUID, Claim> allClaims = new HashMap<>();

    public void registerClaim(Claim claim) {
        allClaims.put(claim.getOwner(), claim);
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
        playerCurrentClaim.put(uuid, claim);
    }
    public Collection<Claim> getAllClaims() {
        return allClaims.values();
    }
    public Claim getClaim(UUID u) {
        return allClaims.get(u);
    }
}
