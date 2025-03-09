package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.dbo.flag.EntityCapFlagDBO;
import net.onelitefeather.pandorascluster.dbo.flag.FlagContainerDBO;
import net.onelitefeather.pandorascluster.dbo.flag.NaturalFlagDBO;
import net.onelitefeather.pandorascluster.dbo.flag.RoleFlagDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;

import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "flag_containers")
public final class FlagContainerEntity implements FlagContainerDBO {

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
    private LandEntity landEntity;

    public FlagContainerEntity() {
        // Empty constructor for Hibernate
    }

    public FlagContainerEntity(Long id,
                               LandEntity landEntity,
                               List<LandNaturalFlagEntity> naturalFlags,
                               List<LandRoleFlagEntity> roleFlags,
                               List<LandEntityCapFlagEntity> entityCapFlags) {
        this.id = id;
        this.naturalFlags = naturalFlags;
        this.roleFlags = roleFlags;
        this.entityCapFlags = entityCapFlags;
        this.landEntity = landEntity;
    }

    @Override
    public Long id() {
        return this.id;
    }

    @Override
    public List<NaturalFlagDBO> naturalFlags() {
        return Collections.unmodifiableList(this.naturalFlags);
    }

    @Override
    public List<EntityCapFlagDBO> entityCapFlags() {
        return Collections.unmodifiableList(this.entityCapFlags);
    }

    @Override
    public List<RoleFlagDBO> roleFlags() {
        return Collections.unmodifiableList(this.roleFlags);
    }

    @Override
    public LandDBO land() {
        return this.landEntity;
    }
}
