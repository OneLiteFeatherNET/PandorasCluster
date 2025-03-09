package net.onelitefeather.pandorascluster.util;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class LocationUtil {

    public static HomePosition of(Location location) {
        return new HomePosition(null,
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    public static ClaimedChunk toClaimedChunk(Chunk chunk) {
        return toClaimedChunk(chunk, null);
    }

    public static ClaimedChunk toClaimedChunk(Chunk chunk, @Nullable LandArea landArea) {
        return new ClaimedChunk(null, chunk.getChunkKey(), landArea);
    }

    public static Location fromHomePosition(HomePosition homePosition) {
        var worldName = homePosition.getWorld();
        var world = Bukkit.getWorld(worldName);
        if (world == null) return Bukkit.getWorlds().getFirst().getSpawnLocation();
        return new Location(world,
                homePosition.getBlockX(),
                homePosition.getBlockY(),
                homePosition.getBlockZ(),
                homePosition.getYaw(),
                homePosition.getPitch());
    }
}
