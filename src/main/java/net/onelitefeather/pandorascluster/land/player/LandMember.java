package net.onelitefeather.pandorascluster.land.player;

import jakarta.persistence.*;
import net.onelitefeather.pandorascluster.enums.LandRole;
import net.onelitefeather.pandorascluster.land.Land;
import org.jetbrains.annotations.NotNull;

@Entity
public class LandMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private LandPlayer member;

    @Enumerated(EnumType.STRING)
    private LandRole role;

    public LandMember() {
    }

    public LandMember(long id, @NotNull LandPlayer member, @NotNull LandRole role) {
        this.id = id;
        this.member = member;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NotNull
    public LandPlayer getMember() {
        return member;
    }

    public void setMember(@NotNull LandPlayer member) {
        this.member = member;
    }

    @NotNull
    public LandRole getRole() {
        return role;
    }

    public void setRole(@NotNull LandRole role) {
        this.role = role;
    }
}
