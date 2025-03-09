package net.onelitefeather.pandorascluster.database.models.player;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Entity
@Table(name = "land_members")
public final class LandMemberEntity implements LandMemberDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private LandPlayerEntity member;

    @Enumerated(EnumType.STRING)
    private LandRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landArea_id")
    private LandAreaEntity landArea;

    public LandMemberEntity() {
        // Empty constructor for Hibernate
    }

    public LandMemberEntity(Long id, LandPlayerEntity member, LandRole role, LandAreaEntity landArea) {
        this.id = id;
        this.member = member;
        this.role = role;
        this.landArea = landArea;
    }

    @Override
    public @Nullable Long id() {
        return id;
    }

    @Override
    public @NotNull LandPlayerDBO member() {
        return member;
    }

    @Override
    public @NotNull LandRole role() {
        return role;
    }

    @Override
    public LandAreaDBO landArea() {
        return this.landArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandMemberEntity that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(member, that.member) && role == that.role && Objects.equals(landArea, that.landArea);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(member);
        result = 31 * result + Objects.hashCode(role);
        result = 31 * result + Objects.hashCode(landArea);
        return result;
    }
}
