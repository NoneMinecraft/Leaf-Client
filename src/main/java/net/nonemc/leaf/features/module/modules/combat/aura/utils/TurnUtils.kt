package net.nonemc.leaf.features.module.modules.combat.aura.utils

import net.nonemc.leaf.features.module.modules.combat.Aura
import net.nonemc.leaf.utils.Rotation
import net.nonemc.leaf.utils.RotationUtils
import net.nonemc.leaf.utils.mc

fun turnYaw(yaw:Float){
    if (!Aura.rotateValue.get()) {
        mc.thePlayer.rotationYaw = yaw
    } else {
        RotationUtils.setTargetRotation(Rotation(yaw, RotationUtils.targetRotation.pitch))
    }
}
 fun turnPitch(pitch:Float){
    if (!Aura.rotateValue.get()) {
        mc.thePlayer.rotationYaw = pitch
    } else {
        RotationUtils.setTargetRotation(Rotation(RotationUtils.targetRotation.yaw,pitch))
    }
}
 fun turn(yaw:Float,pitch:Float,syaw:Float,spitch:Float){
    if (!Aura.rotateValue.get()) {
        mc.thePlayer.rotationYaw = yaw
        mc.thePlayer.rotationPitch = pitch
    } else {
        RotationUtils.setTargetRotation(Rotation(syaw, spitch))
    }
}