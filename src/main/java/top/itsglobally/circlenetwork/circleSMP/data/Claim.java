package top.itsglobally.circlenetwork.circleSMP.data;

import org.bukkit.Location;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Claim {

    private final String name;
    private final Set<Long> coveredChunks;
    private final UUID owner;

    public Claim(String name, UUID u) {
        this.name = name;
        this.coveredChunks = new HashSet<>();
        this.owner = u;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void addRegion(Location l1, Location l2) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX()) >> 4;
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ()) >> 4;
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX()) >> 4;
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ()) >> 4;

        for (int cx = x1; cx <= x2; cx++) {
            for (int cz = z1; cz <= z2; cz++) {
                long key = ((long) cx << 32) | (cz & 0xffffffffL);
                coveredChunks.add(key);
            }
        }
    }

    public boolean containsChunk(long chunkKey) {
        return coveredChunks.contains(chunkKey);
    }

    public Set<Long> getCoveredChunks() {
        return coveredChunks;
    }
}
