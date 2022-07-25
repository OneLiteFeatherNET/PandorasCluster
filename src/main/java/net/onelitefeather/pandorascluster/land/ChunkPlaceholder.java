package net.onelitefeather.pandorascluster.land;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
public class ChunkPlaceholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long chunkIndex;

    public ChunkPlaceholder() {
    }

    public ChunkPlaceholder(@NotNull Long chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public Long getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(@NotNull Long chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    @Override
    public String toString() {
        return "ChunkPlaceholder{" +
                "id=" + id +
                ", chunkIndex=" + chunkIndex +
                '}';
    }
}
