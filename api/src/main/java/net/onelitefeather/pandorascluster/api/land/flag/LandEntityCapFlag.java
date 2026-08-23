package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;

public record LandEntityCapFlag(Long id, String name, int spawnLimit, FlagContainer parent) implements LandFlag {}
