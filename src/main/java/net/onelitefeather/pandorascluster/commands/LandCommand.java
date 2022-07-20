package net.onelitefeather.pandorascluster.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.service.LandService1;
import net.onelitefeather.pandorascluster.util.Util;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LandCommand {

    private final LandService1 landService1;

    public LandCommand(@NotNull PandorasClusterPlugin plugin) {
        this.landService1 = plugin.getWorldChunkManager();
    }

    @CommandMethod("land")
    @CommandDescription("Open the land gui")
    public void execute(@NotNull Player player) {

        WorldChunk worldChunk = this.landService1.getWorldChunk(player.getChunk());
        if (worldChunk == null) {
            player.sendMessage(Component.text("Du befindest du nicht auf einem Chunk"));
            return;
        }

        if (worldChunk.isOwner(player.getUniqueId()) || player.hasPermission("featherchunks.settings.others")) {
            Util.openChunkMainMenu(player, worldChunk).open();
        }
    }
}
