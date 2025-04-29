package net.onelitefeather.pandorascluster.dto.land;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dto.flag.FlagContainerDto;
import net.onelitefeather.pandorascluster.dto.player.LandPlayerDto;
import net.onelitefeather.pandorascluster.dto.position.HomePositionDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LandDto extends PandorasModel {
    @Nullable
    Long id();

    @NotNull
    LandPlayerDto owner();

    @NotNull
    HomePositionDto home();

    @NotNull
    List<LandAreaDto> areas();

    FlagContainerDto flagContainer();
}
