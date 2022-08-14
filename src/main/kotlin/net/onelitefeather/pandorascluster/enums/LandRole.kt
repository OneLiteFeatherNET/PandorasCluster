package net.onelitefeather.pandorascluster.enums

enum class LandRole(val roleName: String, val display: String, val access: Boolean) {
    OWNER("owner", "&4owner", true),
    ADMIN("admin", "&cAdmin", true),
    TRUSTED("trusted", "&aTrusted", true),
    MEMBER("member", "&eMember", true),
    BANNED("banned", "&c&oBanned", false),
    VISITOR("visitor", "&7Visitor", false);


    fun isGrantAble() = this != OWNER
}

val LAND_ROLES = LandRole.values()
fun getLandRole(name: String): LandRole? =
    LAND_ROLES.firstOrNull { landRole ->  landRole.name == name.uppercase() }
