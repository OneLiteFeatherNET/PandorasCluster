package net.onelitefeather.pandorascluster.enums

enum class LandRole(val roleName: String, val display: String, val access: Boolean) {
    OWNER("owner", "&4owner", true),
    ADMIN("admin", "&cAdmin", true),
    TRUSTED("trusted", "&aTrusted", true),
    MEMBER("member", "&eMember", true),
    BANNED("banned", "&c&oBanned", false),
    VISITOR("visitor", "&7Visitor", false);


    fun isGrantAble() = this != OWNER

    companion object {
        @JvmStatic
        val landRoles = values()

        @JvmStatic
        fun getLandRole(name: String): LandRole? =
            landRoles.firstOrNull { landRole ->  landRole.name == name.uppercase() }
    }
}