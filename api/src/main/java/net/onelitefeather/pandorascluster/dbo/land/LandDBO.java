package net.onelitefeather.pandorascluster.dbo.land;

import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;
import net.onelitefeather.pandorascluster.dbo.position.HomePositionDBO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LandDBO {
    @Nullable
    Long id();

    @NotNull
    LandPlayerDBO owner();

    @NotNull
    HomePositionDBO home();

    @NotNull
    List<LandAreaDBO> areas();

    @NotNull
    String world();
}
