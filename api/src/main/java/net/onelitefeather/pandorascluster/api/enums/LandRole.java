package net.onelitefeather.pandorascluster.api.enums;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum LandRole {

    OWNER("owner", "<dark_red>Owner", 100),
    ADMIN("admin", "<red>Admin", 80),
    TRUSTED("trusted", "<green>Trusted", 50),
    MEMBER("member", "<yellow>Member", 40),
    BANNED("banned", "<red><b>Banned", 0),
    VISITOR("visitor", "<gray>Visitor", 10);

    private final String roleName;
    private final String display;
    private final Integer priority;

    private static final LandRole[] LAND_ROLES = values();

    LandRole(String roleName, String display, Integer priority) {
        this.roleName = roleName;
        this.display = display;
        this.priority = priority;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDisplay() {
        return display;
    }

    public Integer getPriority() {
        return priority;
    }

    @Nullable
    public static LandRole getLandRole(String name) {
        return Arrays.stream(LAND_ROLES).filter(landRole -> landRole.toString().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
