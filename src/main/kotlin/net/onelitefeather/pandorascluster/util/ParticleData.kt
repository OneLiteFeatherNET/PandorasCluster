package net.onelitefeather.pandorascluster.util;

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class ParticleData(
    val trustedParticle: Particle,
    val untrustedParticle: Particle,
    val radius: Int,
    val speed: Double,
    val offX: Double,
    val offY: Double,
    val offZ: Double,
    val data: Any?
) {

    fun getExtraData(trusted: Boolean, configurationSection: ConfigurationSection): Any? {

        val dataTypeClass = if(trusted) trustedParticle.dataType else untrustedParticle.dataType
        if (dataTypeClass == Void::class.java) {
            return null
        }
        when (dataTypeClass.simpleName) {

            "DustOptions" -> {
                return DustOptions(
                    Color.fromRGB(
                        configurationSection.getInt("red"),
                        configurationSection.getInt("green"),
                        configurationSection.getInt("blue")),
                         configurationSection.getDouble("size").toFloat()
                )
            }

            "BlockData" -> {
                return Material.matchMaterial(configurationSection.getString("material", "STONE")!!)?.createBlockData()
            }

            "ItemStack" -> {
                return ItemStack(Material.matchMaterial(configurationSection.getString("material", "STONE")!!)!!)
            }

            "Color" -> {
                return Color.fromRGB(
                    configurationSection.getInt("red"),
                    configurationSection.getInt("green"),
                    configurationSection.getInt("blue"))
            }

            else -> return null
        }
    }
}

val DEFAULT_PARTICLE_DATA = ParticleData(Particle.REDSTONE, Particle.FLAME, 64, 3.0, 0.0, 0.0, 0.0, null)
