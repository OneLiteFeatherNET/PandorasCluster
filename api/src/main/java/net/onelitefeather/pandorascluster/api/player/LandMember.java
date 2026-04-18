package net.onelitefeather.pandorascluster.api.player;

import net.onelitefeather.pandorascluster.api.enums.LandRole;

public record LandMember(Long id, LandPlayer member, LandRole role) {

    public Long getId() {
        return id;
    }

    public LandPlayer getMember() {
        return member;
    }

    public LandRole getRole() {
        return role;
    }
}
