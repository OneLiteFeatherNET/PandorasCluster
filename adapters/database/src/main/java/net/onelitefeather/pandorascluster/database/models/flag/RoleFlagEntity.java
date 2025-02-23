package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.dbo.flag.RoleFlagDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "role_flags")
public class RoleFlagEntity implements RoleFlagDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private boolean state;

    @Enumerated(EnumType.STRING)
    private LandRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landArea_id")
    private LandAreaEntity landArea;

    public RoleFlagEntity(Long id, String name, boolean state, LandRole role, LandAreaEntity landArea) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.role = role;
        this.landArea = landArea;
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
    public boolean state() {
        return this.state;
    }

    @Override
    public @NotNull LandRole role() {
        return this.role;
    }

    @Override
    public LandAreaDBO landArea() {
        return this.landArea;
    }

    public LandAreaEntity getLandArea() {
        return landArea;
    }
}
