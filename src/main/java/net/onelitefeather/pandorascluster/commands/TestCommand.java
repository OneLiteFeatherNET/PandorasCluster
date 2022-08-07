package net.onelitefeather.pandorascluster.commands;

import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import net.onelitefeather.pandorascluster.util.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TestCommand {

    private final PandorasClusterApi pandorasClusterApi;

    public TestCommand(PandorasClusterApi pandorasClusterApi) {
        this.pandorasClusterApi = pandorasClusterApi;
    }

    @CommandMethod("testOwner")
    public void testOwner(Player player) {

        Land land = this.pandorasClusterApi.getLandService().getFullLand(player.getChunk());
        if (land == null) return;

        player.sendMessage("Owner name: " + land.getOwner().getName());
        player.sendMessage("Owner Id: " + land.getOwner().getUniqueId());
    }

    @CommandMethod("chunks")
    public void testChunks(Player player) {

        LandPlayer landPlayer = this.pandorasClusterApi.getLandPlayer(player.getUniqueId());
        if (landPlayer == null) return;

        Land land = this.pandorasClusterApi.getLandService().getLand(landPlayer);
        if (land == null) return;

        for (ChunkPlaceholder placeholder : land.getMergedChunks()) {
            Chunk chunk = player.getWorld().getChunkAt(ChunkUtil.getChunkCoordX(placeholder.getChunkIndex()), ChunkUtil.getChunkCoordZ(placeholder.getChunkIndex()));
            int x = chunk.getX() * 16;
            int z = chunk.getZ() * 16;
            Location location = player.getWorld().getHighestBlockAt(x, z).getLocation();
            String command = String.format("/tp %s %s %s", location.getX() + 7.5, location.getY() + 1, location.getZ() + 7.5);
            player.sendMessage(MiniMessage.miniMessage().deserialize(String.format("<white><click:run_command:%s>%s;%s</click></white>", command, chunk.getX(), chunk.getZ())));

        }
    }

    @CommandMethod("home")
    public void testHome(Player player) {


        HomePosition homePosition = this.pandorasClusterApi.getLandService().getHome(player.getUniqueId());
        if (homePosition == null) return;
        player.teleport(HomePosition.fromHomePosition(player.getWorld(), homePosition));

    }
}
