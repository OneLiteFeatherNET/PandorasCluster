package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.land.LandWorld;

public record WorldNaturalFlag(Long id, String name, Boolean state, LandWorld world) { }

