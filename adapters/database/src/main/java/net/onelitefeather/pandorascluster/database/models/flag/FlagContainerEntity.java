package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "flag_containers")
public final class FlagContainerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flagContainer")
    private List<LandNaturalFlagEntity> naturalFlags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flagContainer")
    private List<LandRoleFlagEntity> roleFlags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flagContainer")
    private List<LandEntityCapFlagEntity> entityCapFlags;

    @OneToOne(mappedBy = "flagContainerEntity")
    private LandEntity land;

    public static final FlagContainerEntity EMPTY = new FlagContainerEntity(
            null,
            null,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList());

    public FlagContainerEntity() {
        // Empty constructor for Hibernate
    }

    public FlagContainerEntity(Long id,
                               LandEntity land,
                               List<LandNaturalFlagEntity> naturalFlags,
                               List<LandRoleFlagEntity> roleFlags,
                               List<LandEntityCapFlagEntity> entityCapFlags) {
        this.id = id;
        this.naturalFlags = naturalFlags;
        this.roleFlags = roleFlags;
        this.entityCapFlags = entityCapFlags;
        this.land = land;
    }

    public Long id() {
        return this.id;
    }

    public List<LandNaturalFlagEntity> naturalFlags() {
        return Collections.unmodifiableList(this.naturalFlags);
    }

    public List<LandEntityCapFlagEntity> entityCapFlags() {
        return Collections.unmodifiableList(this.entityCapFlags);
    }

    public List<LandRoleFlagEntity> roleFlags() {
        return Collections.unmodifiableList(this.roleFlags);
    }

    public LandEntity land() {
        return this.land;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlagContainerEntity that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hashCode(id);
    }
}
