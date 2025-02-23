package net.onelitefeather.pandorascluster.dbo.player;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LandMemberDBO {

    @Nullable
    Long id();

    @NotNull
    LandPlayerDBO member();

    @NotNull
    LandRole role();
}
