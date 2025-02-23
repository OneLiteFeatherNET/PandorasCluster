package net.onelitefeather.pandorascluster.api.player;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;

import java.util.Objects;

public final class LandMember implements PandorasModel {

    private final Long id;
    private LandPlayer member;
    private LandRole role;

    public LandMember(Long id, LandPlayer member, LandRole role) {
        this.id = id;
        this.member = member;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public LandPlayer getMember() {
        return member;
    }

    public void setMember(LandPlayer member) {
        this.member = member;
    }

    public LandRole getRole() {
        return role;
    }

    public void setRole(LandRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LandMember that)) return false;

        return Objects.equals(id, that.id) && Objects.equals(member, that.member) && role == that.role;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(member);
        result = 31 * result + Objects.hashCode(role);
        return result;
    }

    @Override
    public String toString() {
        return "LandMember{" +
                "id=" + getId() +
                ", member=" + getMember() +
                ", role=" + getRole() +
                '}';
    }
}
