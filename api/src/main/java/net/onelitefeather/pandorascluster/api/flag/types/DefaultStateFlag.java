package net.onelitefeather.pandorascluster.api.flag.types;

import net.onelitefeather.pandorascluster.api.flag.Flag;

public interface DefaultStateFlag<T> extends Flag<T> {

    Boolean getDefaultState();
    DefaultStateFlag<T> defaultState(Boolean newState);
}
