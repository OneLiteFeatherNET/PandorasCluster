package net.onelitefeather.pandorascluster.extensions

import net.kyori.adventure.text.minimessage.MiniMessage

fun String.toMM() = MiniMessage.miniMessage().deserialize(this)