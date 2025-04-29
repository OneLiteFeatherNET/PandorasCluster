package net.onelitefeather.pandorascluster.dto.player;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dto.land.LandAreaDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LandMemberDto extends PandorasModel {

    @Nullable
    Long id();

    @NotNull
    LandPlayerDto member();

    @NotNull
    LandRole role();

    LandAreaDto landArea();
}
