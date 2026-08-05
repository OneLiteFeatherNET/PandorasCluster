package net.onelitefeather.pandorascluster.database.models.chunk;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;

import java.util.Objects;

@Entity
@Table(
        name = "land_chunks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_land_chunks_area_chunk",
                columnNames = {"landArea_id", "chunk_index"}
        )
)
public final class ClaimedChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chunk_index", nullable = false)
    private Long chunkIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landArea_id", nullable = false)
    private LandAreaEntity landArea;

    public ClaimedChunkEntity() {
        // Empty constructor for Hibernate
    }

    public ClaimedChunkEntity(Long id, Long chunkIndex, LandAreaEntity landArea) {
        this.id = id;
        this.chunkIndex = chunkIndex;
        this.landArea = landArea;
    }

    public Long id() {
        return id;
    }

    public Long chunkIndex() {
        return chunkIndex;
    }

    public LandAreaEntity landArea() {
        return landArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimedChunkEntity that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hashCode(id);
    }
}
