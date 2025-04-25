package net.nonemc.leaf.features.module.modules.combat.aura.utils

import net.nonemc.leaf.libs.data.Rotation
import net.nonemc.leaf.features.module.modules.combat.Aura
import net.nonemc.leaf.features.module.modules.combat.Aura.keepDirectionTickValue
import net.nonemc.leaf.libs.rotation.RotationBaseLib

fun turnYaw(yaw: Float) {
    if (!Aura.rotateValue.get()) {
        net.nonemc.leaf.libs.base.mc.thePlayer.rotationYaw = yaw
    } else {
        RotationBaseLib.setTargetRotation(Rotation(yaw, RotationBaseLib.targetRotation.pitch))
    }
}

fun turnPitch(pitch: Float) {
    if (!Aura.rotateValue.get()) {
        net.nonemc.leaf.libs.base.mc.thePlayer.rotationYaw = pitch
    } else {
        RotationBaseLib.setTargetRotation(Rotation(RotationBaseLib.targetRotation.yaw, pitch))
    }
}

fun turn(yaw: Float, pitch: Float, syaw: Float, spitch: Float) {
    if (!Aura.rotateValue.get()) {
        net.nonemc.leaf.libs.base.mc.thePlayer.rotationYaw = yaw
        net.nonemc.leaf.libs.base.mc.thePlayer.rotationPitch = pitch
    } else {
        RotationBaseLib.setTargetRotation(Rotation(syaw, spitch), keepDirectionTickValue.get())
    }
}