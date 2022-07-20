package net.onelitefeather.pandorascluster.enums;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum LandRole {

    OWNER("owner", "64Owner"),
    ADMIN("admin", "&cAdmin"),
    TRUSTED("trusted", "&aTrusted"),
    MEMBER("member", "&eMember"),
    BANNED("banned", "&c&oBanned"),
    VISITOR("visitor", "&7Visitor");

    private final String name;
    private final String display;

    private static final Map<String, LandRole> BY_NAME = Maps.newHashMap();

    LandRole(String name, String display) {
        this.name = name;
        this.display = display;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDisplay() {
        return display;
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
