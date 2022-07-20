package net.onelitefeather.pandorascluster.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.land.flag.LandFlag;
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity;
import net.onelitefeather.pandorascluster.land.flag.LandFlagHandler;
import net.onelitefeather.pandorascluster.land.player.LandMember;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Entity
public class Land implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private LandPlayer owner;

    @OneToOne(mappedBy = "land")
    private HomePosition homePosition;

    @ElementCollection(targetClass = LandMember.class)
    private List<LandMember> landMembers;

    @ElementCollection(targetClass = LandFlagEntity.class)
    private List<LandFlagEntity> landFlags;

    @ElementCollection(targetClass = Long.class)
    private List<Long> chunks;

    @Column
    private String world;

    @Column
    private int x;

    @Column
    private int z;

    private transient final LandFlagHandler flagHandler;

    public Land() {
        this.flagHandler = new LandFlagHandler(this);
    }

    public Land(@NotNull LandPlayer owner, @NotNull HomePosition homePosition, @NotNull String world, int x, int z) {
        this.owner = owner;
        this.homePosition = homePosition;
        this.world = world;
        this.x = x;
        this.z = z;
        this.flagHandler = new LandFlagHandler(this);
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
    public List<Long> getMergedChunks() {
        return chunks;
    }

    public void setMergedChunks(@NotNull List<Long> chunks) {
        this.chunks = chunks;
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
    public LandFlagEntity getFlag(@NotNull LandFlag landFlag) {

        LandFlagEntity landFlagEntity = null;
        for (int i = 0; i < this.landFlags.size() && landFlagEntity == null; i++) {
            var flagEntity = this.landFlags.get(i);
            var flag = flagEntity.getFlag();
            if (flag != null && flag.equals(landFlag)) landFlagEntity = flagEntity;
        }

        return landFlagEntity;
    }

    public LandFlagHandler getFlagHandler() {
        return flagHandler;
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
    public Land clone() {
        try {
            return (Land) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

}
