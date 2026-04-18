package net.onelitefeather.pandorascluster.database.models.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "land_areas")
public final class LandAreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 64, nullable = false)
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

    public @Nullable Long id() {
        return this.id;
    }

    public @NotNull String name() {
        return this.name;
    }

    public @NotNull List<LandMemberEntity> members() {
        return Collections.unmodifiableList(this.members);
    }

    public @NotNull List<ClaimedChunkEntity> chunks() {
        return Collections.unmodifiableList(this.chunks);
    }

    public LandEntity land() {
        return this.land;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandAreaEntity that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hashCode(id);
    }
}
