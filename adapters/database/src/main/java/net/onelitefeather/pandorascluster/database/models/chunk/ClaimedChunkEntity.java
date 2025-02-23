package net.onelitefeather.pandorascluster.database.models.chunk;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;

@Entity
@Table(name = "land_chunks")
public final class ClaimedChunkEntity implements ClaimedChunkDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long chunkIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landArea_id")
    private LandAreaEntity landArea;

    public ClaimedChunkEntity(Long id, Long chunkIndex, LandAreaEntity landArea) {
        this.id = id;
        this.chunkIndex = chunkIndex;
        this.landArea = landArea;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public Long chunkIndex() {
        return chunkIndex;
    }

    public LandAreaDBO landArea() {
        return landArea;
    }
}
