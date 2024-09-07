package net.onelitefeather.pandorascluster.util;

import org.bukkit.entity.AnimalTamer
import org.bukkit.entity.Tameable

fun isPetOwner(tameable: Tameable, animalTamer: AnimalTamer): Boolean = tameable.owner?.uniqueId == animalTamer.uniqueId
