package net.onelitefeather.pandorascluster;

import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.service.EntityDataStoreService;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

class PandorasClusterApiImpl implements PandorasClusterApi {

    private final PandorasClusterPlugin plugin;
    private final EntityDataStoreService entityDataStoreService;

    public PandorasClusterApiImpl(@NotNull PandorasClusterPlugin plugin) {
        this.plugin = plugin;
        this.entityDataStoreService = new EntityDataStoreService(plugin);
    }

    @Override
    public PandorasClusterPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public EntityDataStoreService getEntityDataStoreService() {
        return entityDataStoreService;
    }

    @Override
    public boolean hasPlayerLand(Player player) {
        return false;
    }

    @Override
    public boolean hasPlayerLand(UUID playerId) {
        return false;
    }

    @Override
    public boolean isChunkClaimed(Chunk chunk) {
        return false;
    }

    @Override
    public boolean isChunkClaimed(int x, int z) {
        return false;
    }

    @Override
    public List<Land> getLands() {
        return null;
    }

    @Override
    public LandPlayer getLandPlayer(@NotNull Player player) {
        return null;
    }

    @Override
    public LandPlayer getLandPlayer(@NotNull UUID uuid) {
        return null;
    }

    @Override
    public LandPlayer getLandPlayer(@NotNull String name) {
        return null;
    }

    @Override
    public List<Land> getLands(Player player) {
        return null;
    }

    @Override
    public boolean hasSameOwner(Land land, Land other) {
        return false;
    }
}
