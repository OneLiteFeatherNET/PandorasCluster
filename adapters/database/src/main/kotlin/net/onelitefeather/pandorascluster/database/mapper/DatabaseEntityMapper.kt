package net.onelitefeather.pandorascluster.database.mapper

interface DatabaseEntityMapper<E, M> {

    fun entityToModel(entity: E?): M?

    fun modelToEntity(model: M?): E?
}