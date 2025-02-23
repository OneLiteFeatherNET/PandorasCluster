package net.onelitefeather.pandorascluster.api.flag;

import net.kyori.adventure.text.Component;

public interface Flag<T> {

    boolean isAllowedInWilderness();

    Flag<T> allowInWilderness(boolean allowWilderness);

    default Component description() {
        return Component.translatable("flag.{0}.description").arguments(Component.text(getName()));
    }

    default Component displayName() {
        return Component.translatable("flag.{0}.displayname").arguments(Component.text(getName()));
    }

    default String getWildernessPermission() {
        return "pandorascluster.wilderness.%s".formatted(getName());
    }

    String getName();
}
