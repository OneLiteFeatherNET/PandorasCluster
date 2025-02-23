package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.dbo.flag.FlagContainerDBO;
import net.onelitefeather.pandorascluster.dbo.flag.RoleFlagDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "role_flags")
public class LandRoleFlagEntity implements RoleFlagDBO {

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
    @JoinColumn(name = "flagContainer_id")
    private FlagContainerEntity flagContainer;

    public LandRoleFlagEntity(Long id, String name, boolean state, LandRole role, FlagContainerEntity flagContainer) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.role = role;
        this.flagContainer = flagContainer;
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
    public FlagContainerDBO flagContainer() {
        return this.flagContainer;
    }

}
