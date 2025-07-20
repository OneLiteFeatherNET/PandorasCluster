package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.dto.flag.EntityCapFlagDto;
import net.onelitefeather.pandorascluster.dto.flag.FlagContainerDto;
import net.onelitefeather.pandorascluster.dto.flag.NaturalFlagDto;
import net.onelitefeather.pandorascluster.dto.flag.RoleFlagDto;
import net.onelitefeather.pandorascluster.dto.land.LandDto;

import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "flag_containers")
public final class FlagContainerEntity implements FlagContainerDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flagContainer")
    private List<LandNaturalFlagEntity> naturalFlags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flagContainer")
    private List<LandRoleFlagEntity> roleFlags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flagContainer")
    private List<LandEntityCapFlagEntity> entityCapFlags;

    @OneToOne
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

    @Override
    public Long id() {
        return this.id;
    }

    public FlagContainerEntity withLand(LandEntity land) {
        this.land = land;
        return this;
    }

    @Override
    public List<NaturalFlagDto> naturalFlags() {
        return Collections.unmodifiableList(this.naturalFlags);
    }

    @Override
    public List<EntityCapFlagDto> entityCapFlags() {
        return Collections.unmodifiableList(this.entityCapFlags);
    }

    @Override
    public List<RoleFlagDto> roleFlags() {
        return Collections.unmodifiableList(this.roleFlags);
    }

    @Override
    public LandDto land() {
        return this.land;
    }
}
