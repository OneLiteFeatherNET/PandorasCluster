package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.dbo.flag.EntityCapFlagDBO;
import net.onelitefeather.pandorascluster.dbo.flag.FlagContainerDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "entityCap_flags")
public final class LandEntityCapFlagEntity implements EntityCapFlagDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer spawnLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flagContainer_id")
    private FlagContainerEntity flagContainer;

    public LandEntityCapFlagEntity(Long id, String name, Integer spawnLimit, FlagContainerEntity flagContainer) {
        this.id = id;
        this.name = name;
        this.spawnLimit = spawnLimit;
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
    public Integer spawnLimit() {
        return this.spawnLimit;
    }

    @Override
    public FlagContainerDBO flagContainer() {
        return this.flagContainer;
    }
}
