package net.onelitefeather.pandorascluster.api.land;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.flag.types.RoleFlag;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.api.util.PlayerUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public record LandArea(Long id,
                       Long landId,
                       String name,
                       List<ClaimedChunk> chunks,
                       List<LandMember> members) {

    public LandArea {
        chunks = List.copyOf(chunks);
        members = List.copyOf(members);
    }

    public Long getId() {
        return id;
    }

    public Long getLandId() {
        return landId;
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

    /**
     * Evaluates whether a caller may interact with this area given a role flag.
     * Returns a structured {@link AccessDecision} so consumers can surface the
     * concrete reason for allow/deny instead of a bare boolean.
     */
    public AccessDecision evaluateAccess(UUID uuid, RoleFlag flag, Land land) {
        if (land.isOwner(uuid)) {
            return AccessDecision.allowed(AccessDecision.Allowed.Reason.OWNER);
        }

        var member = getMember(uuid);
        if (member == null) {
            if (hasVisitorAccess(flag)) {
                return AccessDecision.allowed(AccessDecision.Allowed.Reason.VISITOR_FLAG);
            }
            if (PlayerUtil.Instances.instance.hasPermission(uuid, flag.getWildernessPermission())) {
                return AccessDecision.allowed(AccessDecision.Allowed.Reason.WILDERNESS_PERMISSION);
            }
            return AccessDecision.denied(AccessDecision.Denied.Reason.NOT_MEMBER);
        }

        if (member.getRole() == LandRole.BANNED) {
            return AccessDecision.denied(AccessDecision.Denied.Reason.BANNED);
        }

        boolean access = member.getRole().getPriority() >= flag.getRole().getPriority();
        if (member.getRole() == LandRole.MEMBER && access && !isAdminOnline(land)) {
            return AccessDecision.denied(AccessDecision.Denied.Reason.ADMIN_OFFLINE_GUARD);
        }
        if (access) {
            return AccessDecision.allowed(AccessDecision.Allowed.Reason.ROLE);
        }
        if (PlayerUtil.Instances.instance.hasPermission(uuid, flag.getWildernessPermission())) {
            return AccessDecision.allowed(AccessDecision.Allowed.Reason.WILDERNESS_PERMISSION);
        }
        return AccessDecision.denied(AccessDecision.Denied.Reason.INSUFFICIENT_ROLE);
    }

    /**
     * Boolean convenience wrapper over {@link #evaluateAccess}. Prefer
     * {@code evaluateAccess} when the concrete outcome is needed (e.g. to show
     * the right message to the player).
     */
    public boolean hasMemberAccess(UUID uuid, RoleFlag flag, Land land) {
        return evaluateAccess(uuid, flag, land).isAllowed();
    }

    public boolean hasVisitorAccess(RoleFlag flag) {
        return flag.getRole().getPriority() <= LandRole.VISITOR.getPriority();
    }

    public boolean isBanned(UUID uuid) {
        var member = getMember(uuid);
        if (member == null) return false;
        return member.getRole() == LandRole.BANNED;
    }

    public boolean isAdmin(UUID uuid, Land land) {
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

    private boolean isAdminOnline(Land land) {
        if (PlayerUtil.Instances.instance.isOnline(land.getOwner().getUniqueId())) return true;
        Predicate<LandMember> isAdminOnline = landMember -> PlayerUtil.Instances.instance.isOnline(landMember.getMember().getUniqueId());
        return members.stream()
                .filter(isAdminOnline)
                .anyMatch(landMember -> isAdmin(landMember.getMember().getUniqueId(), land));
    }
}
