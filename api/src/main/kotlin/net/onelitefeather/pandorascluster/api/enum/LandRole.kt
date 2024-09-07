package net.onelitefeather.pandorascluster.api.enum

enum class LandRole(val roleName: String, val display: String, val access: Boolean) {
    OWNER("owner", "<dark_red>Owner", true),
    ADMIN("admin", "<red>Admin", true),
    TRUSTED("trusted", "<green>Trusted", true),
    MEMBER("member", "<yellow>Member", true),
    BANNED("banned", "<red><b>Banned", false),
    VISITOR("visitor", "<gray>Visitor", false);

    fun isGrantAble() = this != OWNER
}

val LAND_ROLES = LandRole.entries.toTypedArray()
fun getLandRole(name: String): LandRole? =
    LAND_ROLES.firstOrNull { landRole ->  landRole.name == name.uppercase() }