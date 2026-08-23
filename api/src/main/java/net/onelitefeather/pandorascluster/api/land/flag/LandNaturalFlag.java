package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;

public record LandNaturalFlag(Long id, String name, Boolean state, FlagContainer parent) implements LandFlag { }
