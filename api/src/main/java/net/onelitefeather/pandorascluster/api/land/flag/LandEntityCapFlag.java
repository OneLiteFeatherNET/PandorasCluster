package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.service.FlagRegistryService;
import net.onelitefeather.pandorascluster.api.flag.types.EntityCapFlag;
import org.jetbrains.annotations.Nullable;

public record LandEntityCapFlag(Long id, String name, int spawnLimit, FlagContainer parent) implements LandFlag {}
