package net.onelitefeather.pandorascluster.api.mapper;

import org.jetbrains.annotations.Nullable;

public interface DatabaseEntityMapper<E, M> {

    @Nullable
    M entityToModel(@Nullable E entity);

    @Nullable
    E modelToEntity(@Nullable M model);
}
