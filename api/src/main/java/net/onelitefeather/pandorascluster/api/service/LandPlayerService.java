package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface LandPlayerService {

    /**
     * @param landArea the landArea of the land
     * @param member the member to add
     * @param landRole the land role
     */
    void addLandMember(@NotNull LandArea landArea, @NotNull LandPlayer member, @Nullable LandRole landRole);

    void updateLandMember(@NotNull LandArea landArea, @NotNull LandMember member);

    /**
     * @param member the member to remove
     */
    void removeLandMember(@NotNull LandMember member);

    @NotNull
    List<LandPlayer> getLandPlayers();

    /**
     * @param uuid the uuid of the player.
     * @param name the name of the player.
     */
    boolean createPlayer(@NotNull UUID uuid, @NotNull String name);

    /**
     * @param uuid the uuid of the player.
     */
    void deletePlayer(@NotNull UUID uuid);

    @Nullable
    LandPlayer getLandPlayer(@NotNull UUID uuid);

    /**
     * @param uuid the uuid of the player.
     * @return true if the player exists
     */
    boolean playerExists(@NotNull UUID uuid);

    /**
     * @param landPlayer the player to update
     */
    void updateLandPlayer(@NotNull LandPlayer landPlayer);

}
