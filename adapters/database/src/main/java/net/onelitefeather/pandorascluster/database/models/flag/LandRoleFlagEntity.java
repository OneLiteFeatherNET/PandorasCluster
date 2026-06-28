package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.dto.flag.FlagContainerDto;
import net.onelitefeather.pandorascluster.dto.flag.RoleFlagDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(
        name = "role_flags",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_role_flags_container_name",
                columnNames = {"flagContainer_id", "name"}
        )
)
public final class LandRoleFlagEntity implements RoleFlagDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "state", nullable = false)
    private boolean state;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 32, nullable = false)
    private LandRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flagContainer_id", nullable = false)
    private FlagContainerEntity flagContainer;

    public LandRoleFlagEntity() {
        // Empty constructor for Hibernate
    }

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
    public FlagContainerDto flagContainer() {
        return this.flagContainer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandRoleFlagEntity that)) return false;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hashCode(id);
    }
}
