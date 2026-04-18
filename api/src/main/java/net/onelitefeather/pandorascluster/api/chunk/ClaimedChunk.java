package net.onelitefeather.pandorascluster.api.chunk;

import org.jetbrains.annotations.NotNull;

public record ClaimedChunk(Long id, @NotNull Long chunkIndex) {

    public Long getId() {
        return id;
    }

    @NotNull
    public Long getChunkIndex() {
        return chunkIndex;
    }
}
