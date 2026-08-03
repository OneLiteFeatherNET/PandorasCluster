package net.onelitefeather.pandorascluster.database.models.land;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.database.models.flag.WorldNaturalFlagEntity;

import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "land_worlds")
public class LandWorldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", length = 36, nullable = false, unique = true)
    private String uuid;

    @Column(name = "name", length = 36, nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "world")
    private List<WorldNaturalFlagEntity> naturalFlags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "worldEntity")
    private List<LandEntity> lands;

    public LandWorldEntity() {
        //Default Constructor
    }

    public LandWorldEntity(Long id, String uuid, String name,
                           List<WorldNaturalFlagEntity> naturalFlags,
                           List<LandEntity> lands) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.naturalFlags = naturalFlags;
        this.lands = lands;
    }

    public Long id() {
        return this.id;
    }

    public String uuid() { return this.uuid; }

    public String name() { return this.name; }

    public List<LandEntity>  lands() { return this.lands; }

    public List<WorldNaturalFlagEntity> naturalFlags() {
        return Collections.unmodifiableList(this.naturalFlags);
    }
}
