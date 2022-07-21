package net.onelitefeather.pandorascluster.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.menus.LandMainMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record LandCommand(PandorasClusterApi api) {

    public LandCommand(@NotNull PandorasClusterApi api) {
        this.api = api;
    }

    @CommandMethod("land")
    @CommandDescription("Open the land gui")
    public void execute(@NotNull Player player) {

        Land land = this.api.getLand(player.getChunk());
        if (land == null) {
            player.sendMessage(Component.text("Du befindest du nicht auf einem Chunk"));
            return;
        }

        if (land.isOwner(player.getUniqueId()) || player.hasPermission("featherchunks.settings.others")) {
            new LandMainMenu(player, 5, "Land Main Menu", land).open();
        }
    }
}
