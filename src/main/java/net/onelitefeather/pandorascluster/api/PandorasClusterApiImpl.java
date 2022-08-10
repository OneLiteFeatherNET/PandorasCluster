package net.onelitefeather.pandorascluster.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.service.LandService;
import net.onelitefeather.pandorascluster.service.DatabaseService;
import net.onelitefeather.pandorascluster.service.EntityDataStoreService;
import net.onelitefeather.pandorascluster.service.LandPlayerService;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class PandorasClusterApiImpl implements PandorasClusterApi {

    private final PandorasClusterPlugin plugin;
    private final EntityDataStoreService entityDataStoreService;
    private final DatabaseService databaseService;
    private final LandPlayerService landPlayerService;
    private final LandService landService;

    public PandorasClusterApiImpl(@NotNull PandorasClusterPlugin plugin) {
        this.plugin = plugin;
        this.entityDataStoreService = new EntityDataStoreService(this);

        FileConfiguration config = plugin.getConfig();

        String jdbcUrl = config.getString("database.jdbcUrl", "'jdbc:mariadb://localhost:3306/pandorascluster?useSSL=false'");
        String databaseDriver = config.getString("database.driver", "org.mariadb.jdbc.Driver");
        String username = config.getString("database.username", "root");
        String password = config.getString("database.password", "%Schueler90");

        this.databaseService = new DatabaseService(jdbcUrl, username, password, databaseDriver);
        this.databaseService.init();

        this.landPlayerService = new LandPlayerService(this);
        this.landService = new LandService(this);
    }

    @Override
    public @NotNull PandorasClusterPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull EntityDataStoreService getEntityDataStoreService() {
        return entityDataStoreService;
    }

    @Override
    public boolean hasPlayerLand(@NotNull Player player) {
        return hasPlayerLand(player.getUniqueId());
    }

    @Override
    public boolean hasPlayerLand(@NotNull UUID playerId) {
        return this.landService.hasPlayerLand(playerId);
    }

    @Override
    public boolean isChunkClaimed(@NotNull Chunk chunk) {
        return this.landService.isChunkClaimed(chunk);
    }

    @Override
    public @NotNull List<Land> getLands() {
        return Objects.requireNonNull(this.landService.getLands());
    }

    @Override
    public LandPlayer getLandPlayer(@NotNull Player player) {
        return this.landPlayerService.getLandPlayer(player.getUniqueId());
    }

    @Override
    public LandPlayer getLandPlayer(@NotNull UUID uuid) {
        return this.landPlayerService.getLandPlayer(uuid);
    }

    @Override
    public @Nullable LandPlayer getLandPlayer(@NotNull String name) {
        return this.landPlayerService.getLandPlayer(name);
    }

    @Override
    public @NotNull List<Land> getLands(@NotNull Player player) {

        List<Land> lands = new ArrayList<>();
        for (Land landEntry : getLands()) {
            if (landEntry.getOwner().getUniqueId().equals(player.getUniqueId()) || landEntry.hasAccess(player.getUniqueId())) {
                lands.add(landEntry);
            }
        }

        return lands;
    }

    @Override
    public @NotNull SessionFactory getSessionFactory() {
        return this.getDatabaseService().getSessionFactory();
    }

    @Override
    public @NotNull LandPlayerService getLandPlayerService() {
        return this.landPlayerService;
    }

    @Override
    public DatabaseService getDatabaseService() {
        return this.databaseService;
    }

    @Override
    public @NotNull LandService getLandService() {
        return this.landService;
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.plugin.getLogger();
    }

    @Override
    public @NotNull Component translateLegacyCodes(@NotNull String text) {
        return MiniMessage.miniMessage().deserialize(
                MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text)));
    }

    @Override
    public @Nullable Land getLand(@NotNull Chunk chunk) {
        return this.landService.getFullLand(chunk);
    }

    @Override
    public void registerPlayer(@NotNull UUID uuid, @NotNull String name, Consumer<Boolean> consumer) {
        this.landPlayerService.createPlayer(uuid, name, consumer);
    }
}
