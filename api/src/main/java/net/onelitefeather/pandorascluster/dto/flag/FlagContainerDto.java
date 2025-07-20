package net.onelitefeather.pandorascluster.dto.flag;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dto.land.LandDto;

import java.util.List;

public interface FlagContainerDto extends PandorasModel {

    Long id();

    List<NaturalFlagDto> naturalFlags();

    List<EntityCapFlagDto> entityCapFlags();

    List<RoleFlagDto> roleFlags();

    LandDto land();
}
