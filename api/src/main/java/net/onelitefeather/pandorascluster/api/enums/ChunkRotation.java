package net.onelitefeather.pandorascluster.api.enums;

public enum ChunkRotation {

    NORTH(0, -1),
    NORTH_EAST(1, -1),

    EAST(1, 0),
    SOUTH_EAST(1, 1),

    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),

    WEST(1, 0),
    NORTH_WEST(-1, -1);

    private static final ChunkRotation[] CHUNK_ROTATIONS = values();
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
}
