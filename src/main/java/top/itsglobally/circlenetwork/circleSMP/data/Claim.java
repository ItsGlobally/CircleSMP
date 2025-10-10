package top.itsglobally.circlenetwork.circleSMP.data;

import org.bukkit.Location;
import top.itsglobally.circlenetwork.circleSMP.managers.ClaimManager;
import top.itsglobally.circlenetwork.circleSMP.managers.DataManager;

import java.util.*;

public class Claim {

    private final String name;
    private final Set<Long> coveredChunks;
    private final UUID owner;
    private final Map<UUID, Set<DataManager.ClaimPerms>> colabs;
    private final UUID id;
    public Claim(String name, UUID u, UUID id) {
        this.name = name;
        this.coveredChunks = new HashSet<>();
        this.colabs = new HashMap<>();
        this.owner = u;
        this.id = id;
    }
    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Map<UUID, Set<DataManager.ClaimPerms>> getColabs() {
        return colabs;
    }
    public boolean isInColab(UUID u) {
        return colabs.get(u) != null;
    }
    public void addColab(UUID u, DataManager.ClaimPerms c) {
        Set<DataManager.ClaimPerms> cs = new HashSet<>();
        cs.add(c);
        colabs.put(u, cs);
    }
    public void addColab(UUID u, Set<DataManager.ClaimPerms> cs) {
        colabs.put(u, cs);
    }
    public void addColab(UUID u) {
        Set<DataManager.ClaimPerms> cs = new HashSet<>();
        cs.add(DataManager.ClaimPerms.MOVEINTO);
        cs.add(DataManager.ClaimPerms.PLACE);
        cs.add(DataManager.ClaimPerms.BREAK);
        cs.add(DataManager.ClaimPerms.INTERACT);
        colabs.put(u, cs);
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
