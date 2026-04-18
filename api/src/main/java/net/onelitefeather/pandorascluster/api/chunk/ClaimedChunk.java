package net.onelitefeather.pandorascluster.api.chunk;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;

public record ClaimedChunk(Long id, @NotNull Long chunkIndex) implements PandorasModel {

    public Long getId() {
        return id;
    }

    @NotNull
    public Long getChunkIndex() {
        return chunkIndex;
    }
}
