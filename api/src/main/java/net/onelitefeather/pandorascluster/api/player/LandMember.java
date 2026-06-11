package net.onelitefeather.pandorascluster.api.player;

import net.onelitefeather.pandorascluster.api.enums.LandRole;

public record LandMember(Long id, LandPlayer member, LandRole role) {}
