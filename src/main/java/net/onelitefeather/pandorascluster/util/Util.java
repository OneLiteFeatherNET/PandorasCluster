package net.onelitefeather.pandorascluster.util;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.menus.ChunkMainMenu;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Util {

    private static final PandorasClusterPlugin PLUGIN = PandorasClusterPlugin.getPlugin(PandorasClusterPlugin.class);

    public static List<Player> getPlayersInChunk(Chunk chunk) {

        List<Player> players = new ArrayList<>();
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Player player) {
                players.add(player);
            }
        }

        return players;
    }


    public static ChunkMainMenu openChunkMainMenu(Player player, Land land) {


        Chunk playerChunk = player.getChunk();

        if (playerChunk.getX() != land.getX() && playerChunk.getZ() != land.getZ()) {
            land = PLUGIN.getWorldChunkManager().getWorldChunk(playerChunk);
        }

        return new ChunkMainMenu(PLUGIN, player, land);

    }
}
