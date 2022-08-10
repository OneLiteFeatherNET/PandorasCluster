package net.onelitefeather.pandorascluster.land.flag

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.land.Land
import org.hibernate.Hibernate

@Entity
data class LandFlagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column
    val name: String? = null,
    @Column
    val value: String? = null,
    @Column
    val type: Byte? = null,
    @Column
    @Enumerated(EnumType.STRING)
    val flagType: LandFlagType? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_id")
    val land: Land? = null
) {

    fun <T : Any> getValue(): T? {
        val result = when(this.type?.toInt()) {
            0 -> this.value
            1 -> this.value?.toIntOrNull()
            2 -> this.value?.toBooleanStrictOrNull()
            3 -> this.value?.toDoubleOrNull()
            4 -> this.value?.toFloatOrNull()
            5 -> this.value?.toShortOrNull()
            6 -> this.value?.toByteOrNull()
            else -> null
        }
        return result as T?
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as LandFlagEntity

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , value = $value , type = $type , flagType = $flagType )"
    }

}
