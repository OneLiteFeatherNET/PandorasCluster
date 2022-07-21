package net.onelitefeather.pandorascluster.enums;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum LandRole {

    OWNER("owner", "64Owner", true),
    ADMIN("admin", "&cAdmin", true),
    TRUSTED("trusted", "&aTrusted", true),
    MEMBER("member", "&eMember", true),
    BANNED("banned", "&c&oBanned", false),
    VISITOR("visitor", "&7Visitor", false);

    private final String name;
    private final String display;
    private final boolean access;

    private static final Map<String, LandRole> BY_NAME = Maps.newHashMap();

    LandRole(String name, String display, boolean access) {
        this.name = name;
        this.display = display;
        this.access = access;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDisplay() {
        return display;
    }

    public boolean hasAccess() {
        return access;
    }

    public static LandRole getChunkRole(String name) {

        LandRole landRole = null;
        for (Map.Entry<String, LandRole> entry : BY_NAME.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                landRole = entry.getValue();
                break;
            }
        }

        return landRole != null ? landRole : VISITOR;
    }

    @Override
    public String toString() {
        return "ChunkRole{" +
                "name='" + name + '\'' +
                ", display='" + display + '\'' +
                '}';
    }

    static {
        for (LandRole landRole : values()) {
            BY_NAME.put(landRole.getName(), landRole);
        }
    }
}
