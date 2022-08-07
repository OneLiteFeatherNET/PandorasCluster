package net.onelitefeather.pandorascluster.api;

import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.service.LandService;
import net.onelitefeather.pandorascluster.service.DatabaseService;
import net.onelitefeather.pandorascluster.service.EntityDataStoreService;
import net.onelitefeather.pandorascluster.service.LandFlagService;
import net.onelitefeather.pandorascluster.service.LandPlayerService;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

public interface PandorasClusterApi {

    @NotNull
    PandorasClusterPlugin getPlugin();

    @NotNull
    EntityDataStoreService getEntityDataStoreService();

    boolean hasPlayerLand(@NotNull Player player);

    boolean hasPlayerLand(@NotNull UUID playerId);

    boolean isChunkClaimed(@NotNull Chunk chunk);

    /**
     * @return A list of all {@link Land}'s
     */
    @NotNull
    List<Land> getLands();

    @Nullable
    LandPlayer getLandPlayer(@NotNull Player player);

    @Nullable
    LandPlayer getLandPlayer(@NotNull UUID uuid);

    @Nullable
    LandPlayer getLandPlayer(@NotNull String name);

    /**
     * @param player the player
     * @return A list of all {@link Land}'s where the player has access.
     */
    @NotNull
    List<Land> getLands(Player player);

    @NotNull
    SessionFactory getSessionFactory();

    @NotNull
    LandPlayerService getLandPlayerService();

    DatabaseService getDatabaseService();

    @NotNull
    LandService getLandService();

    @NotNull
    LandFlagService getLandFlagService();

    @NotNull
    Logger getLogger();

    @NotNull
    Component translateLegacyCodes(@NotNull String text);

    @Nullable
    Land getLand(@NotNull Chunk chunk);

    void registerPlayer(@NotNull UUID uuid, @NotNull String name, Consumer<Boolean> consumer);
}
