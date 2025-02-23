package net.onelitefeather.pandorascluster.database.models.flag;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.dbo.flag.NaturalFlagDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "natural_flags")
public class NaturalFlagEntity implements NaturalFlagDBO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private boolean state;

    public NaturalFlagEntity(Long id, String name, boolean state) {
        this.id = id;
        this.name = name;
        this.state = state;
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
}
