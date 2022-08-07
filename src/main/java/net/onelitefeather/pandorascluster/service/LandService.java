package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.builder.LandBuilder;
import net.onelitefeather.pandorascluster.enums.ChunkRotation;
import net.onelitefeather.pandorascluster.enums.LandRole;
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandMember;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import net.onelitefeather.pandorascluster.listener.LandBlockListener;
import net.onelitefeather.pandorascluster.listener.LandEntityListener;
import net.onelitefeather.pandorascluster.listener.LandPlayerListener;
import net.onelitefeather.pandorascluster.listener.LandWorldListener;
import net.onelitefeather.pandorascluster.util.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

public class LandService {

    private final PandorasClusterApi pandorasClusterApi;

    public LandService(PandorasClusterApi pandorasClusterApi) {
        this.pandorasClusterApi = pandorasClusterApi;

        PluginManager pluginManager = pandorasClusterApi.getPlugin().getServer().getPluginManager();

        LandFlagService landFlagService = this.pandorasClusterApi.getLandFlagService();
        pluginManager.registerEvents(new LandBlockListener(this, landFlagService), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandEntityListener(this, landFlagService), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandPlayerListener(this, landFlagService), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandWorldListener(this, landFlagService), pandorasClusterApi.getPlugin());
    }


    public List<Land> getLands() {
        List<Land> lands = new ArrayList<>();
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var query = session.createQuery("SELECT l FROM Land l", Land.class);
            lands.addAll(query.list());
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not load lands.", e);
        }

        return lands;
    }

    public boolean hasPlayerLand(@NotNull UUID playerId) {

        OfflinePlayer offlinePlayer = this.pandorasClusterApi.getPlugin().getServer().getOfflinePlayer(playerId);
        if (!offlinePlayer.hasPlayedBefore()) return false;

        LandPlayer landPlayer = this.pandorasClusterApi.getLandPlayer(playerId);
        if (landPlayer == null) return false;
        return getLand(landPlayer) != null;
    }

    @Nullable
    public HomePosition getHome(@NotNull UUID uuid) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var landOfOwner = session.createQuery("SELECT h FROM Land l JOIN l.homePosition h JOIN l.owner p WHERE p.uuid = :uuid", HomePosition.class);
            landOfOwner.setParameter("uuid", uuid.toString());
            return landOfOwner.getSingleResult();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
        }

        return null;
    }

    public void createLand(@NotNull LandPlayer owner, @NotNull Player player, @NotNull Chunk chunk) {

        if (!this.hasPlayerLand(owner.getUniqueId())) {

            Transaction transaction = null;

            try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                HomePosition homePosition = HomePosition.of(player.getLocation());
                session.persist(homePosition);

                Land land = new LandBuilder().
                        owner(owner).
                        home(homePosition).
                        world(player.getWorld()).
                        chunkX(chunk.getX()).
                        chunkZ(chunk.getZ()).
                        members(List.of()).
                        mergedChunks(List.of()).
                        build();

                session.persist(land);


                this.pandorasClusterApi.getLandFlagService().addFlags(land);

                transaction.commit();
                addChunkPlaceholder(chunk, land);
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
            }
        }
    }

    public void deletePlayerLand(@NotNull Player player) {

        LandPlayer landPlayer = this.pandorasClusterApi.getLandPlayer(player.getUniqueId());
        if (landPlayer == null) return;

        Land land = getLand(landPlayer);
        if (land == null) return;

        World world = player.getServer().getWorld(land.getWorld());
        if (world == null) return;

        if (exists(land)) {
            Transaction transaction = null;
            try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.remove(land);
                transaction.commit();
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }

                this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot remove the land from the database.", e);
            }
        }
    }

    public boolean exists(@NotNull Land land) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var kitCooldown = session.createQuery("SELECT kdc FROM Land kdc WHERE playerId = :playerId AND id = :id", Land.class);
            kitCooldown.setMaxResults(1);
            kitCooldown.setParameter("playerId", land.getOwner().getUniqueId().toString());
            kitCooldown.setParameter("id", land.getId());
            return kitCooldown.uniqueResult() != null;
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Something went wrong!", e);
            return false;
        }
    }

    public boolean isChunkClaimed(@NotNull Chunk chunk) {
        return this.getFullLand(chunk) != null;
    }

    public void findConnectedChunk(@NotNull Player player, @NotNull Consumer<Land> consumer) {
        ChunkRotation chunkRotation = ChunkRotation.getChunkRotation(player.getFacing().name());
        if (chunkRotation != null) {

            Chunk chunk = player.getChunk();
            Chunk connectedChunk = player.getWorld().getChunkAt(
                    chunk.getX() + chunkRotation.getX(),
                    chunk.getZ() + chunkRotation.getZ());

            Land land = this.getFullLand(connectedChunk);
            consumer.accept(land);
        }
    }

    @Nullable
    public Land getLand(@NotNull LandPlayer owner) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var landOfOwner = session.createQuery("SELECT l FROM Land l JOIN l.owner o JOIN FETCH l.chunks WHERE o.uuid = :uuid", Land.class);
            landOfOwner.setParameter("uuid", owner.getUniqueId().toString());
            return landOfOwner.uniqueResult();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
        }

        return null;
    }

    @Nullable
    public Land getFullLand(@NotNull Chunk chunk) {
        Land land = null;
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {

            var query = session.createQuery("SELECT ch FROM ChunkPlaceholder ch JOIN FETCH ch.land WHERE ch.chunkIndex = :chunkIndex", ChunkPlaceholder.class);
            query.setParameter("chunkIndex", ChunkUtil.getChunkIndex(chunk));
            var chunkHolder = query.uniqueResult();
            if (chunkHolder != null) {
                land = chunkHolder.getLand();
            }
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
        }
        return land;
    }

    public void setLandOwner(@NotNull Land land, @NotNull LandPlayer landPlayer) {
        land.setOwner(landPlayer);
        updateLand(land);
    }

    public void addLandMember(@NotNull Land land, @NotNull LandPlayer member, @Nullable LandRole landRole) {
        LandMember landMember = new LandMember(member, landRole != null ? landRole : LandRole.MEMBER);
        land.getLandMembers().add(landMember);
        updateLand(land);
    }

    public void addChunkPlaceholder(Chunk chunk, Land land) {

        Transaction transaction = null;
        ChunkPlaceholder chunkPlaceholder = new ChunkPlaceholder(ChunkUtil.getChunkIndex(chunk), land);

        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(chunkPlaceholder);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Cannot save chunk placeholder %s", chunkPlaceholder), e);
        }
    }

    private void updateLand(@NotNull Land land) {
        Transaction transaction = null;
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(land);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Cannot update land %s", land), e);
        }
    }
}

