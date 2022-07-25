package net.onelitefeather.pandorascluster.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.enums.LandRole;
import net.onelitefeather.pandorascluster.land.flag.LandFlag;
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity;
import net.onelitefeather.pandorascluster.land.player.LandMember;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import net.onelitefeather.pandorascluster.util.ChunkUtil;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Land implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private LandPlayer owner;

    @OneToOne
    private HomePosition homePosition;

    @OneToMany
    private List<LandMember> landMembers;

    @OneToMany
    private List<LandFlagEntity> landFlags;

    @OneToMany
    private List<ChunkPlaceholder> chunks;

    @Column
    private String world;

    @Column
    private int x;

    @Column
    private int z;

    public Land() {
        this.landMembers = new ArrayList<>();
        this.chunks = new ArrayList<>();
        this.landFlags = new ArrayList<>();
    }

    public Land(@NotNull LandPlayer owner, @NotNull HomePosition homePosition, @NotNull String world, int x, int z) {
        this();
        this.owner = owner;
        this.homePosition = homePosition;
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    public LandPlayer getOwner() {
        return owner;
    }

    public void setOwner(@NotNull LandPlayer owner) {
        this.owner = owner;
    }

    @NotNull
    public HomePosition getHomePosition() {
        return homePosition;
    }

    public void setHomePosition(@NotNull HomePosition homePosition) {
        this.homePosition = homePosition;
    }

    @NotNull
    public List<LandMember> getLandMembers() {
        return landMembers;
    }

    public void setLandMembers(@NotNull List<LandMember> landMembers) {
        this.landMembers = landMembers;
    }

    @NotNull
    public List<LandFlagEntity> getLandFlags() {
        return landFlags;
    }

    public void setLandFlags(@NotNull List<LandFlagEntity> landFlags) {
        this.landFlags = landFlags;
    }

    @NotNull
    public List<ChunkPlaceholder> getMergedChunks() {
        return chunks;
    }

    public boolean isChunkConnected(@NotNull Chunk chunk) {

        boolean connected = false;
        for (int i = 0; i < this.chunks.size() && !connected; i++) {
            ChunkPlaceholder mergedChunk = this.chunks.get(i);
            connected = ChunkUtil.getChunkIndex(chunk) == mergedChunk.getChunkIndex();
        }

        return connected;
    }

    public void setMergedChunks(@NotNull List<ChunkPlaceholder> chunks) {
        this.chunks = chunks;
    }

    public void mergeChunk(@NotNull Chunk chunk) {
        mergeChunk(chunk.getX(), chunk.getZ());
    }

    public void mergeChunk(int chunkX, int chunkZ) {
        this.chunks.add(new ChunkPlaceholder(ChunkUtil.getChunkIndex(chunkX, chunkZ)));
    }

    @NotNull
    public String getWorld() {
        return world;
    }

    public void setWorld(@NotNull String world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void addFlag(@NotNull LandFlagEntity landFlag) {
        this.landFlags.add(landFlag);
    }

    @Nullable
    public LandMember getLandMember(@NotNull UUID uuid) {

        LandMember landMember = null;

        for (int i = 0; i < this.getLandMembers().size() && landMember == null; i++) {
            LandMember member = this.getLandMembers().get(i);
            if (member.getMember().getUniqueId().equals(uuid)) {
                landMember = member;
            }
        }

        return landMember;

    }

    @Nullable
    public LandFlagEntity getFlag(@NotNull LandFlag landFlag) {

        LandFlagEntity landFlagEntity = null;
        for (int i = 0; i < this.landFlags.size() && landFlagEntity == null; i++) {
            var flagEntity = this.landFlags.get(i);
            var flag = flagEntity.getFlag();
            if (flag != null && flag.equals(landFlag)) landFlagEntity = flagEntity;
        }

        return landFlagEntity;
    }

    public boolean hasAccess(@NotNull UUID uuid) {
        LandMember landMember = getLandMember(uuid);
        if (landMember == null) return false;
        return landMember.getRole().hasAccess();
    }

    public boolean isOwner(@NotNull UUID uniqueId) {
        return this.getOwner().getUniqueId().equals(uniqueId);
    }

    @Override
    public String toString() {
        return "Land{" +
                "owner=" + owner +
                ", world='" + world + '\'' +
                ", x=" + x +
                ", z=" + z +
                '}';
    }

    @Override
    @NotNull
    public Land clone() throws CloneNotSupportedException {
        return (Land) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Land land)) return false;

        if (id != land.id) return false;
        if (x != land.x) return false;
        if (z != land.z) return false;
        if (!owner.equals(land.getOwner())) return false;
        if (!Objects.equals(homePosition, land.homePosition)) return false;
        if (!Objects.equals(landMembers, land.landMembers)) return false;
        if (!Objects.equals(landFlags, land.landFlags)) return false;
        if (!Objects.equals(chunks, land.chunks)) return false;
        return Objects.equals(world, land.world);
    }

    public boolean isMerged() {
        return !this.getMergedChunks().isEmpty();
    }

    public boolean isBanned(UUID uniqueId) {
        LandMember landMember = getLandMember(uniqueId);
        return landMember != null && landMember.getRole() == LandRole.BANNED;
    }
}
