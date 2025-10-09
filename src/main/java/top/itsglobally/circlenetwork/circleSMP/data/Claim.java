package top.itsglobally.circlenetwork.circleSMP.data;

import org.bukkit.Location;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Claim {

    private final String name;
    private final Set<Long> coveredChunks;
    private final UUID owner;
    private final Set<UUID> colabs;

    public Claim(String name, UUID u) {
        this.name = name;
        this.coveredChunks = new HashSet<>();
        this.colabs = new HashSet<>();
        this.owner = u;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Set<UUID> getColabs() {
        return colabs;
    }
    public boolean isInColab(UUID u) {
        return colabs.contains(u);
    }
    public void addColab(UUID u) {
        colabs.add(u);
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

    public void addChunk(long key) {
        coveredChunks.add(key);
    }

    public Set<Long> getCoveredChunks() {
        return coveredChunks;
    }
    public void removeColab(UUID u) {
        colabs.remove(u);
    }

    public void removeRegion(Location l1, Location l2) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX()) >> 4;
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ()) >> 4;
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX()) >> 4;
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ()) >> 4;

        for (int cx = x1; cx <= x2; cx++) {
            for (int cz = z1; cz <= z2; cz++) {
                long key = ((long) cx << 32) | (cz & 0xffffffffL);
                coveredChunks.remove(key);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Claim)) return false;
        Claim other = (Claim) o;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

}
