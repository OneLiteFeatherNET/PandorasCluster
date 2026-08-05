package net.onelitefeather.pandorascluster.api.service.result.player;

import net.onelitefeather.pandorascluster.api.player.LandMember;

public sealed interface AddLandMemberResult {

    record Added(LandMember flag) implements AddLandMemberResult {}
    record AlreadyAdded(String message) implements AddLandMemberResult {}
    record Failed(String message, Throwable throwable) implements AddLandMemberResult {}
}
