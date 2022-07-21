package net.onelitefeather.pandorascluster.util;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
}
