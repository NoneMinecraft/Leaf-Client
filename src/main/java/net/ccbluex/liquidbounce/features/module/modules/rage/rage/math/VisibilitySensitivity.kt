package net.ccbluex.liquidbounce.features.module.modules.rage.rage.math

import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.enableAccumulation
import net.ccbluex.liquidbounce.features.module.modules.rage.RageBot.visibilitySensitivityTick

private var vsvt = 0

fun vs(can:Boolean): Boolean {
    if (can){
        if (vsvt < visibilitySensitivityTick.get()){
            vsvt ++
            return false
        }else return true
    }else{
        if (enableAccumulation.get()) {vsvt = 0}
        return false
    }
}