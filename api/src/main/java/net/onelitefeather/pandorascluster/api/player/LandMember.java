package net.onelitefeather.pandorascluster.api.player;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;

public record LandMember(Long id, LandPlayer member, LandRole role) implements PandorasModel {

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
