package net.onelitefeather.pandorascluster.dbo.land;

import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.dbo.flag.FlagContainerDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LandDBO extends PandorasModel {
    @Nullable
    Long id();

    @NotNull
    LandPlayerDBO owner();

    @NotNull
    HomePositionDBO home();

    @NotNull
    List<LandAreaDBO> areas();

    FlagContainerDBO flagContainer();
}
