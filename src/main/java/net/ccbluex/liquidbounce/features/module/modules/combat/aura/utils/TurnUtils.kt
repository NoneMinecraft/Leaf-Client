package net.ccbluex.liquidbounce.features.module.modules.combat.aura.utils

import net.ccbluex.liquidbounce.features.module.modules.combat.Aura
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.mc

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