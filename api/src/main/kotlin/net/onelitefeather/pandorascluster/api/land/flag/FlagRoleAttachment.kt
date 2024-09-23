package net.onelitefeather.pandorascluster.api.land.flag

import net.onelitefeather.pandorascluster.api.enums.LandRole

data class FlagRoleAttachment(
    val id: Long?,
    val role: LandRole = LandRole.MEMBER,
    var value: String = "null",
    val flag: LandFlag = LandFlag.UNKNOWN
) {

    fun <T : Any> getValue(): T? {
        val result = when (this.flag.type.toInt()) {
            0 -> this.value
            1 -> this.value.toIntOrNull()
            2 -> this.value.toBooleanStrictOrNull()
            3 -> this.value.toDoubleOrNull()
            4 -> this.value.toFloatOrNull()
            5 -> this.value.toShortOrNull()
            6 -> this.value.toByteOrNull()
            else -> null
        }
        return result as T?

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlagRoleAttachment) return false

        if (id != other.id) return false
        if (role != other.role) return false
        if (value != other.value) return false
        if (flag != other.flag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + role.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + flag.hashCode()
        return result
    }

    override fun toString(): String {
        return "FlagRoleAttachment(id=$id, role=$role, value='$value', flag=$flag)"
    }

    companion object {
        fun getDefaultFlag(landFlag: LandFlag): FlagRoleAttachment =
            FlagRoleAttachment(null, LandRole.VISITOR, landFlag.defaultValue.toString(), landFlag)

        fun isValidValue(landFlag: LandFlag, value: String): Boolean {
            return when(landFlag.type.toInt()) {
                0 -> true
                1 -> value.toIntOrNull() != null
                2 -> value.toBooleanStrictOrNull() != null
                3 -> value.toDoubleOrNull() != null
                4 -> value.toFloatOrNull() != null
                5 -> value.toShortOrNull() != null
                6 -> value.toByteOrNull() != null
                else -> false
            }
        }

    }
}