package net.onelitefeather.pandorascluster.api.service.result.player;

import net.onelitefeather.pandorascluster.api.player.LandPlayer;

public sealed interface DeleteLandPlayerResult {

    record Deleted(LandPlayer player) implements DeleteLandPlayerResult {}
    record NotFound()  implements DeleteLandPlayerResult {}
    record Failed(String message, Throwable cause) implements DeleteLandPlayerResult {}
}
