package net.onelitefeather.pandorascluster.database.models.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "land_areas")
public final class LandAreaEntity implements LandAreaDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "landArea")
    private List<LandMemberEntity> members;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "landArea")
    private List<ClaimedChunkEntity> chunks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    private LandEntity land;

    public LandAreaEntity() {
        // Empty constructor for Hibernate
    }

    public LandAreaEntity(Long id,
                          String name,
                          List<LandMemberEntity> members,
                          List<ClaimedChunkEntity> chunks,
                          LandEntity landEntity) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.chunks = chunks;
        this.land = landEntity;
    }

    @Override
    public @Nullable Long id() {
        return this.id;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public @NotNull List<LandMemberDBO> members() {
        return Collections.unmodifiableList(this.members);
    }

    @Override
    public @NotNull List<ClaimedChunkDBO> chunks() {
        return Collections.unmodifiableList(this.chunks);
    }

    @Override
    public LandDBO land() {
        return this.land;
    }
}
