package net.onelitefeather.pandorascluster.api.chunk;

import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ClaimedChunk implements PandorasModel {

    private final Long id;
    private Long chunkIndex;
    private LandArea landArea;

    public ClaimedChunk(Long id, Long chunkIndex, LandArea landArea) {
        this.id = id;
        this.chunkIndex = chunkIndex;
        this.landArea = landArea;
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

    public LandArea getLandArea() {
        return landArea;
    }

    public void setLandArea(LandArea landArea) {
        this.landArea = landArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimedChunk that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(chunkIndex, that.chunkIndex) && Objects.equals(landArea, that.landArea);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(chunkIndex);
        result = 31 * result + Objects.hashCode(landArea);
        return result;
    }

    @Override
    public String toString() {
        return "ClaimedChunk{" +
                "id=" + getId() +
                ", chunkIndex=" + getChunkIndex() +
                ", landArea=" + getLandArea() +
                '}';
    }
}
