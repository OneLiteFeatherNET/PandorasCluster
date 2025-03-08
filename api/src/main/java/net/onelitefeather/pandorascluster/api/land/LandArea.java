package net.onelitefeather.pandorascluster.api.land;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.mapper.PandorasModel;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.api.util.PlayerUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public final class LandArea implements PandorasModel {

    private final Long id;
    private String name;

    private final List<ClaimedChunk> chunks;
    private final List<LandMember> members;
    private Land land;

    public LandArea(Long id,
                    String name,
                    List<ClaimedChunk> chunks,
                    List<LandMember> members,
                    Land land) {
        this.id = id;
        this.name = name;
        this.chunks = chunks;
        this.members = members;
        this.land = land;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<ClaimedChunk> getChunks() {
        return chunks;
    }

    public List<LandMember> getMembers() {
        return members;
    }

    public void setLand(Land land) {
        this.land = land;
    }

    public Land getLand() {
        return land;
    }

    public boolean hasMemberAccess(UUID uuid, RoleFlag flag) {
        if (land.isOwner(uuid)) return true;

        var hasVisitorAccess = hasVisitorAccess(flag);
        var member = getMember(uuid);
        if (member == null) return hasVisitorAccess;

        var access = member.getRole().getPriority() >= flag.getRole().getPriority();
        var hasMemberRoleAccess = member.getRole() == LandRole.MEMBER && access && !isAdminOnline();
        if (hasMemberRoleAccess) return false;
        if (access) return true;
        return PlayerUtil.Instances.instance.hasPermission(uuid, flag.getWildernessPermission());
    }

    public boolean hasVisitorAccess(RoleFlag flag) {
        return flag.getRole().getPriority() <= LandRole.VISITOR.getPriority();
    }

    public boolean isBanned(UUID uuid) {
        var member = getMember(uuid);
        if (member == null) return false;
        return member.getRole() == LandRole.BANNED;
    }

    public boolean isAdmin(UUID uuid) {
        if (land.isOwner(uuid)) return true;
        var member = getMember(uuid);
        if (member == null) return false;
        return member.getRole() == LandRole.ADMIN;
    }

    public boolean isChunkMerged(Long chunkIndex) {
        return chunks.stream().anyMatch(claimedChunk -> claimedChunk.getChunkIndex().equals(chunkIndex));
    }

    public boolean hasMemberRole(UUID uuid, LandRole role) {
        return members.stream().anyMatch(member -> member.getMember().getUniqueId().equals(uuid) && member.getRole() == role);
    }

    public LandMember getMember(UUID uuid) {
        return members.stream().filter(landMember -> landMember.getMember().getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    private boolean isAdmin(LandMember landMember) {
        return isAdmin(landMember.getMember().getUniqueId());
    }

    private boolean isAdminOnline() {
        if (PlayerUtil.Instances.instance.isOnline(land.getOwner().getUniqueId())) return true;
        Predicate<LandMember> isAdminOnline = landMember -> PlayerUtil.Instances.instance.isOnline(landMember.getMember().getUniqueId());
        return members.stream().filter(isAdminOnline).anyMatch(this::isAdmin);
    }
}
