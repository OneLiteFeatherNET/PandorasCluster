package net.onelitefeather.pandorascluster.api.mapper

interface DatabaseEntityMapper<E, M> {

    fun entityToModel(entity: E?): M?

    fun modelToEntity(model: M?): E?
}