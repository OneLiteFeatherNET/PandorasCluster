package net.onelitefeather.pandorascluster.dbo.flag;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;

import java.util.List;

public interface FlagContainerDBO extends PandorasModel {

    Long id();

    List<NaturalFlagDBO> naturalFlags();

    List<EntityCapFlagDBO> entityCapFlags();

    List<RoleFlagDBO> roleFlags();

    LandDBO land();
}
