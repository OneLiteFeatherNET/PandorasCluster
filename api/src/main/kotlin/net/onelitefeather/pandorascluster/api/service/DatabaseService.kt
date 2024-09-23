package net.onelitefeather.pandorascluster.api.service

import net.onelitefeather.pandorascluster.database.mapper.impl.*
import org.hibernate.SessionFactory
import java.nio.file.Path

interface DatabaseService {

    fun connect(configFilePath: Path)

    fun shutdown()

    fun isRunning(): Boolean

    fun sessionFactory(): SessionFactory

    fun landMapper(): LandEntityMapper
    fun landMemberMapper(): LandMemberMapper
    fun landPlayerMapper(): LandPlayerMapper
    fun claimedChunkMapper(): ClaimedChunkMapper
    fun homePositionMapper(): HomePositionMapper
    fun flagMapper(): FlagRoleAttachmentMapper
}