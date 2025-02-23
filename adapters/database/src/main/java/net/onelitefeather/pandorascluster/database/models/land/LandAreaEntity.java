package net.onelitefeather.pandorascluster.database.models.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.flag.EntityCapFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.NaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.flag.RoleFlagEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO;
import net.onelitefeather.pandorascluster.dbo.flag.EntityCapFlagDBO;
import net.onelitefeather.pandorascluster.dbo.flag.NaturalFlagDBO;
import net.onelitefeather.pandorascluster.dbo.flag.RoleFlagDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandMemberDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "land_areas")
public class LandAreaEntity implements LandAreaDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "landAreaEntity")
    private final List<LandMemberEntity> members;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "landAreaEntity")
    private final List<ClaimedChunkEntity> chunks;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "landAreaEntity")
    private final List<RoleFlagEntity> roleFlags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "landAreaEntity")
    private final List<NaturalFlagEntity> naturalFlags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    private LandEntity land;

    public LandAreaEntity(Long id,
                          String name,
                          List<LandMemberEntity> members,
                          List<ClaimedChunkEntity> chunks,
                          List<RoleFlagEntity> roleFlags,
                          List<NaturalFlagEntity> naturalFlags,
                          LandEntity land) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.chunks = chunks;
        this.roleFlags = roleFlags;
        this.naturalFlags = naturalFlags;
        this.land = land;
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
        return this.members.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @NotNull List<ClaimedChunkDBO> chunks() {
        return this.chunks.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @NotNull List<RoleFlagDBO> roleFlags() {
        return this.roleFlags.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @NotNull List<NaturalFlagDBO> naturalFlags() {
        return this.naturalFlags.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public LandDBO land() {
        return this.land;
    }
}
