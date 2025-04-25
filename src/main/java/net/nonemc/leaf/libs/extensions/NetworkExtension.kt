package net.nonemc.leaf.libs.extensions

import net.minecraft.client.network.NetworkPlayerInfo

fun NetworkPlayerInfo.getFullName(): String {
    if (displayName != null) {
        return displayName!!.formattedText
    }

    val team = playerTeam
    val name = gameProfile.name
    return team?.formatString(name) ?: name
}