package net.onelitefeather.pandorascluster.builder;

import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder;
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity;
import net.onelitefeather.pandorascluster.land.player.LandMember;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LandBuilder {

    private Land land;

    public LandBuilder(@NotNull Land origin) {
        try {
            this.land = origin.clone();
        } catch (CloneNotSupportedException e) {
            this.land = new Land();
        }
    }

    public LandBuilder() {
        this.land = new Land();
    }

    public LandBuilder owner(@NotNull LandPlayer owner) {
        this.land.setOwner(owner);
        return this;
    }

    public LandBuilder home(@NotNull HomePosition homePosition) {
        this.land.setHomePosition(homePosition);
        return this;
    }

    public LandBuilder home(@NotNull Location location) {
        this.land.setHomePosition(HomePosition.of(location));
        return this;
    }

    public LandBuilder world(@Nullable World world) {
        this.land.setWorld(world != null ? world.getName() : Bukkit.getWorlds().get(0).getName());
        return this;
    }

    public LandBuilder chunkX(int x) {
        this.land.setX(x);
        return this;
    }

    public LandBuilder chunkZ(int z) {
        this.land.setZ(z);
        return this;
    }

    public LandBuilder mergedChunks(@NotNull List<ChunkPlaceholder> mergedChunks) {
        this.land.setMergedChunks(mergedChunks);
        return this;
    }

    public LandBuilder members(@NotNull List<LandMember> landMembers) {
        this.land.setLandMembers(landMembers);
        return this;
    }

    public Land build() {
        return this.land;
    }

}
