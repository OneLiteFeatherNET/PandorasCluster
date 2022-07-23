package net.onelitefeather.pandorascluster.enums;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public enum ChunkRotation {

    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(-1, -1);

    private static final Map<String, BlockFace> BLOCK_FACE_NAMES = Maps.newHashMap();
    public static final Map<String, ChunkRotation> BY_NAME = Maps.newHashMap();
    private final int x;
    private final int z;

    ChunkRotation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Nullable
    public static ChunkRotation getChunkRotation(@NotNull String name) {
        return BY_NAME.get(name);
    }

    @NotNull
    public static BlockFace getBlockFace(@NotNull String name) {
        return BLOCK_FACE_NAMES.getOrDefault(name, BlockFace.SELF);
    }

    @NotNull
    public static BlockFace getBlockFace(@NotNull Location location) {

        BlockFace face = BlockFace.SELF;
        Iterator<BlockFace> iterator = BLOCK_FACE_NAMES.values().iterator();
        while (iterator.hasNext() && face == BlockFace.SELF) {
            BlockFace blockFace = iterator.next();
            if (blockFace.getDirection().equals(location.getDirection())) {
                face = blockFace;
            }
        }

        return face;
    }

    static {
        for (ChunkRotation chunkRotation : ChunkRotation.values()) {
            BY_NAME.put(chunkRotation.name(), chunkRotation);
        }

        for (BlockFace blockFace : BlockFace.values()) {
            BLOCK_FACE_NAMES.put(blockFace.name(), blockFace);
        }
    }
}
