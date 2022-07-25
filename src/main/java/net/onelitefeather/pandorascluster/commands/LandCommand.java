package net.onelitefeather.pandorascluster.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.Component;
import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.menus.LandMainMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record LandCommand(PandorasClusterApi api) {

    @CommandMethod("land")
    @CommandDescription("Open the land gui")
    public void execute(@NotNull Player player) {

        LandPlayer landPlayer = this.api.getLandPlayer(player.getUniqueId());
        if(landPlayer == null) return;

        Land land = this.api.getLandService().getLand(landPlayer);
        if (land == null) {
            player.sendMessage(Component.text("Nichts gefunden!"));
            return;
        }

        player.sendMessage(land.toString());

//        if (land.isOwner(player.getUniqueId()) || player.hasPermission("featherchunks.settings.others")) {
//            new LandMainMenu(player, 5, "Land Main Menu", land).open();
//        }
    }
}
