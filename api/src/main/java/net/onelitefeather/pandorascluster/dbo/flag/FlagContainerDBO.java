package net.onelitefeather.pandorascluster.dbo.flag;

import net.onelitefeather.pandorascluster.dbo.land.LandDBO;

import java.util.List;

public interface FlagContainerDBO {

    Long id();

    List<NaturalFlagDBO> naturalFlags();

    List<EntityCapFlagDBO> entityCapFlags();

    List<RoleFlagDBO> roleFlags();

    LandDBO land();
}
