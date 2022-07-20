package net.onelitefeather.pandorascluster.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.player.ChunkPlayer;
import net.onelitefeather.pandorascluster.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class LandPlayerService {

    private final PandorasClusterPlugin pandorasClusterPlugin;
    private final List<ChunkPlayer> chunkPlayers;

    public LandPlayerService(@NotNull PandorasClusterPlugin pandorasClusterPlugin) {
        this.pandorasClusterPlugin = pandorasClusterPlugin;
        this.chunkPlayers = new ArrayList<>();
    }

    public List<ChunkPlayer> getChunkPlayers() {
        return this.chunkPlayers;
    }

    @Nullable
    public ChunkPlayer getChunkPlayer(@NotNull UUID uuid) {
        for (int i = 0; i < this.chunkPlayers.size(); i++) {
            ChunkPlayer player = this.chunkPlayers.get(i);
            if (player.getUniqueId().equals(uuid)) {
                return player;
            }
        }

        return null;
    }

    @Nullable
    public ChunkPlayer getChunkPlayer(@NotNull String name) {
        ChunkPlayer chunkPlayer = null;
        List<ChunkPlayer> players = this.chunkPlayers;
        for (int i = 0; i < players.size() && chunkPlayer == null; i++) {
            ChunkPlayer player = players.get(i);
            if (player.getName().equalsIgnoreCase(name)) {
                chunkPlayer = player;
            }
        }

        return null;
    }

    public void load() {
        Bukkit.getOnlinePlayers().forEach(this::loadOnlinePlayer);
    }

    public void createPlayer(@NotNull UUID uuid, @NotNull String name, int availableChunkClaims, Consumer<Boolean> consumer) {
        playerExists(uuid, exists -> {

            if (!exists) {
                ChunkPlayer chunkPlayer = new ChunkPlayer(uuid, name, availableChunkClaims, 0);
                this.chunkPlayers.add(chunkPlayer);
                updateChunkPlayer(chunkPlayer);
            }

            consumer.accept(!exists);
        });
    }

    public boolean deletePlayer(@NotNull UUID uuid) {

        ChunkPlayer chunkPlayer = getChunkPlayer(uuid);
        if (chunkPlayer == null) return false;
        try (Session session = this.pandorasClusterPlugin.getDatabaseService().getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(chunkPlayer);
            session.getTransaction().commit();
            this.chunkPlayers.remove(chunkPlayer);
        } catch (HibernateException e) {
            this.pandorasClusterPlugin.getLogger().log(Level.SEVERE, String.format("Could not delete player data for %s", uuid), e);
        }

        return true;
    }

    @Nullable
    public ChunkPlayer fromDatabase(@NotNull UUID uuid) {
        try (Session session = this.pandorasClusterPlugin.getDatabaseService().getSessionFactory().openSession()) {
            var chunkPlayerQuery = session.createQuery("SELECT cp FROM ChunkPlayer cp WHERE uuid = :uuid", ChunkPlayer.class);
            chunkPlayerQuery.setMaxResults(1);
            chunkPlayerQuery.setParameter("uuid", uuid.toString());
            return chunkPlayerQuery.getSingleResult();
        } catch (HibernateException e) {
            this.pandorasClusterPlugin.getLogger().log(Level.SEVERE, String.format("Could not load player data for %s", uuid), e);
        }

        return null;
    }

    public void playerExists(@NotNull UUID uuid, @NotNull Consumer<Boolean> consumer) {

        boolean exists = false;
        try (Session session = this.pandorasClusterPlugin.getDatabaseService().getSessionFactory().openSession()) {
            var chunkPlayerQuery = session.createQuery("SELECT cp FROM ChunkPlayer cp WHERE uuid = :uuid", ChunkPlayer.class);
            chunkPlayerQuery.setMaxResults(1);
            chunkPlayerQuery.setParameter("uuid", uuid.toString());
            exists = chunkPlayerQuery.list().size() > 0;
        } catch (HibernateException e) {
            this.pandorasClusterPlugin.getLogger().log(Level.SEVERE, String.format("Could not load player data for %s", uuid), e);
        }

        consumer.accept(exists);
    }

    public void updateChunkPlayer(@NotNull ChunkPlayer chunkPlayer) {
        try (Session session = this.pandorasClusterPlugin.getDatabaseService().getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(chunkPlayer);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            this.pandorasClusterPlugin.getLogger().log(Level.SEVERE, String.format("Could not update player data %s", chunkPlayer.getUniqueId()), e);
        }
    }

    public void loadOnlinePlayer(@NotNull Player player) {

        CompletableFuture.runAsync(() -> playerExists(player.getUniqueId(), exists -> {
            if (exists) {

                ChunkPlayer chunkPlayer = fromDatabase(player.getUniqueId());
                if (chunkPlayer != null) {
                    this.chunkPlayers.add(chunkPlayer);
                    player.sendMessage(translateLegacyCodes(String.format("%s &7Deine &eSpielerdaten &7wurden &aerfolgreich &7geladen!", player.getName())));
                }

            } else {

                this.createPlayer(player.getUniqueId(), player.getName(), Util.getChunksByPermission(player), createSuccess -> {
                    if (createSuccess) {
                        player.sendMessage(translateLegacyCodes(String.format("&7Hey, %s! &7Deine &eSpielerdaten &7wurden &aerfolgreich &7erstellt!", player.getName())));
                    } else {
                        player.sendMessage(translateLegacyCodes("&7Deine &eSpielerdaten &7konnten leider &cnicht &7erstellt werden!"));
                    }
                });
            }

        }));
    }

    private Component translateLegacyCodes(String text) {
        return MiniMessage.miniMessage().deserialize(MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text)));
    }

    public void removeOnlinePlayer(@NotNull Player player) {
        ChunkPlayer chunkPlayer = getChunkPlayer(player.getUniqueId());
        if (chunkPlayer != null) {
            this.chunkPlayers.remove(chunkPlayer);
        }
    }
}
