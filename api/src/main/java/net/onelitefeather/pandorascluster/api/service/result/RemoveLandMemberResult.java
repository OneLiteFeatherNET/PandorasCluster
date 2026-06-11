package net.onelitefeather.pandorascluster.api.service.result;

import net.onelitefeather.pandorascluster.api.player.LandMember;

public sealed interface RemoveLandMemberResult {

    record Removed(LandMember flag) implements RemoveLandMemberResult {}
    record Failed(String message, Throwable cause) implements RemoveLandMemberResult {}
}
