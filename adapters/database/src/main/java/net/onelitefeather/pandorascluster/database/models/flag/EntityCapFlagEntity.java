package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.dbo.flag.EntityCapFlagDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "entityCap_flags")
public class EntityCapFlagEntity implements EntityCapFlagDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer spawnLimit;

    public EntityCapFlagEntity(Long id, String name, Integer spawnLimit) {
        this.id = id;
        this.name = name;
        this.spawnLimit = spawnLimit;
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
}
