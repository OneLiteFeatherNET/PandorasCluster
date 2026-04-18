package net.onelitefeather.pandorascluster.api.land;

import net.onelitefeather.pandorascluster.api.enums.LandRole;

/**
 * Discriminated union describing the outcome of a land-access check. Every
 * variant records the concrete reason so callers can surface precise messages
 * ("you are banned" vs. "you lack wilderness permission") instead of a bare
 * boolean. Exhaustive switch on {@code AccessDecision} forces callers to
 * decide how to present each outcome — adding a new variant surfaces every
 * stale call site as a compile error.
 */
public sealed interface AccessDecision {

    /** The caller is allowed through. The nested {@link Reason} records why. */
    record Allowed(Reason reason) implements AccessDecision {
        public enum Reason {
            /** The acting player owns the land. */
            OWNER,
            /** Visitor access is granted by the flag's default role. */
            VISITOR_FLAG,
            /** The member's role satisfies the flag's required role. */
            ROLE,
            /** No direct access, but the wilderness permission node was held. */
            WILDERNESS_PERMISSION
        }
    }

    /** The caller is blocked. The nested {@link Reason} records why. */
    record Denied(Reason reason) implements AccessDecision {
        public enum Reason {
            /** The caller's role is {@code BANNED}. */
            BANNED,
            /** The caller is not a member and the flag denies visitors. */
            NOT_MEMBER,
            /** The caller's role does not meet the flag's required role. */
            INSUFFICIENT_ROLE,
            /** Member has the role but the admin-offline guard blocks the action. */
            ADMIN_OFFLINE_GUARD,
            /** Fallback — the caller lacked the wilderness permission. */
            NO_PERMISSION
        }
    }

    /** Convenience predicate so boolean-only callers can keep their shape. */
    default boolean isAllowed() {
        return this instanceof Allowed;
    }

    /**
     * Factory for the most common allowed case — owner/role/etc. The variant
     * enums are exposed for structured messaging.
     */
    static AccessDecision allowed(Allowed.Reason reason) {
        return new Allowed(reason);
    }

    static AccessDecision denied(Denied.Reason reason) {
        return new Denied(reason);
    }
}
