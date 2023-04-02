package net.onelitefeather.pandorascluster.land.flag

import jakarta.persistence.*
import net.onelitefeather.pandorascluster.land.Land
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.hibernate.Hibernate

@Entity
data class LandFlagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column
    val name: String? = null,
    @Column
    var value: String? = null,
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
        val result = when (this.type?.toInt()) {
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

fun isValidValue(landFlag: LandFlag, value: String): Boolean {
    return when(landFlag.type.toInt()) {
        0 -> !StringUtils.isNumeric(value) && !BooleanUtils.isNotFalse(value.toBoolean()) && !BooleanUtils.isNotTrue(value.toBoolean())
        1 -> value.toIntOrNull() != null
        2 -> value.toBooleanStrictOrNull() != null
        3 -> value.toDoubleOrNull() != null
        4 -> value.toFloatOrNull() != null
        5 -> value.toShortOrNull() != null
        6 -> value.toByteOrNull() != null
        else -> false
    }
}

fun getDefaultFlag(landFlag: LandFlag) : LandFlagEntity = LandFlagEntity(
    landFlag.ordinal.toLong(), landFlag.name,
    landFlag.defaultValue.toString(), landFlag.type,
    landFlag.landFlagType,
    null)
