package net.onelitefeather.pandorascluster.util;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static HomePosition of(Location location) {
        return new HomePosition(null,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    public static ClaimedChunk toClaimedChunk(Chunk chunk) {
        return new ClaimedChunk(null, chunk.getChunkKey());
    }

    public static Location fromHomePosition(World world, HomePosition homePosition) {
        return new Location(world,
                homePosition.getBlockX(),
                homePosition.getBlockY(),
                homePosition.getBlockZ(),
                homePosition.yaw(),
                homePosition.pitch());
    }
}
