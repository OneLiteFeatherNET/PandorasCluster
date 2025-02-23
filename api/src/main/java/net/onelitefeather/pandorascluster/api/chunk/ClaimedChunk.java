package net.onelitefeather.pandorascluster.api.chunk;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ClaimedChunk {

    private final Long id;
    private Long chunkIndex;

    public ClaimedChunk(Long id, Long chunkIndex) {
        this.id = id;
        this.chunkIndex = chunkIndex;
    }

    public Long getId() {
        return id;
    }

    @NotNull
    public Long getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Long chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimedChunk that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(chunkIndex, that.chunkIndex);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(chunkIndex);
        return result;
    }

    @Override
    public String toString() {
        return "ClaimedChunk{" +
                "id=" + getId() +
                ", chunkIndex=" + getChunkIndex() +
                '}';
    }
}
