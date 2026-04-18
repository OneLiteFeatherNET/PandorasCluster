package net.onelitefeather.pandorascluster.api.land.flag;

import net.onelitefeather.pandorascluster.api.flag.FlagContainer;

/**
 * Discriminated union over every kind of flag that can be attached to a
 * {@link FlagContainer}. Exhaustive switch on this type forces call sites to
 * handle every flag variant — the compiler rejects any new variant that is not
 * yet wired in.
 */
public sealed interface LandFlag permits LandNaturalFlag, LandRoleFlag, LandEntityCapFlag {

    Long id();

    String name();

    FlagContainer parent();
}
