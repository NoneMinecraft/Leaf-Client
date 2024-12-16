/*
 * Leaf Hacked Client
 * Code by NoneMinecraft
 */
 package net.ccbluex.liquidbounce.features.module.modules.rage

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.rage.rage.control.idle
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "AntiAim", category = ModuleCategory.Rage)
class AntiAim : Module() {
    private val yaw = FloatValue("Yaw",45F,-180F,180F)
    private val pitch = FloatValue("Pitch",45F,-90F,90F)
    private val auto = BoolValue("Auto",true)
    private val yawOffset = FloatValue("AutoYawOffset",45F,-180F,180F)
    private val rotateValue = BoolValue("SilentRotate", true)
    private val dynamic = BoolValue("Dynamic", true)
    private val dynamicAmplitude = FloatValue("DynamicAmplitude",15F,0F,180F)
    private val sneakMode = ListValue("SneakMode", arrayOf("Packet","Legit","Off"),"Off")
    private val whenRageBotIsIdle = BoolValue("WhenRageBotIsIdle", true)
    private var yawValue = 0F
    private var pitchValue = 0F
    private var rotationTick = 0
    override fun onDisable() {
        yawValue = 0F
        pitchValue = 0F
        rotationTick = 0
    }
    // "WeaponConfig": {
    //      "state": true,
    //      "keybind": 0,
    //      "array": true,
    //      "autodisable": "NONE",
    //      "trigger": "TOGGLE",
    //      "values": {
    //        "Weapons": "P250",
    //        "HitBox-Head-AK": true,
    //        "HitBox-Chest-AK": true,
    //        "HitBox-Feet-AK": true,
    //        "Priority-AK": "Head",
    //        "PitchOffset-AK": 0.23,
    //        "TargetPredictSize-AK": 2.52,
    //        "FireMode-AK": "Packet",
    //        "FireTick-AK": 1,
    //        "NoSpreadBaseTick-AK": 1,
    //        "SilentRotate-AK": false,
    //        "HitBox-Head-M4A1": true,
    //        "HitBox-Chest-M4A1": true,
    //        "HitBox-Feet-M4A1": true,
    //        "Priority-M4A1": "Head",
    //        "PitchOffset-M4A1": 0.05,
    //        "TargetPredictSize-M4A1": 2.6,
    //        "FireMode-M4A1": "Packet",
    //        "FireTick-M4A1": 1,
    //        "NoSpreadBaseTick-M4A1": 2,
    //        "SilentRotate-M4A1": false,
    //        "HitBox-Head-AWP": true,
    //        "HitBox-Chest-AWP": true,
    //        "HitBox-Feet-AWP": true,
    //        "Priority-AWP": "Head",
    //        "PitchOffset-AWP": 0.0,
    //        "TargetPredictSize-AWP": 2.52,
    //        "FireMode-AWP": "Packet",
    //        "FireTick-AWP": 1,
    //        "NoSpreadBaseTick-AWP": 2,
    //        "SilentRotate-AWP": false,
    //        "HitBox-Head-Shotgun": true,
    //        "HitBox-Chest-Shotgun": true,
    //        "HitBox-Feet-Shotgun": true,
    //        "Priority-Shotgun": "Feet",
    //        "PitchOffset-Shotgun": 0.05,
    //        "TargetPredictSize-Shotgun": 2.18,
    //        "FireMode-Shotgun": "Packet",
    //        "FireTick-Shotgun": 1,
    //        "NoSpreadBaseTick-Shotgun": 5,
    //        "SilentRotate-Shotgun": false,
    //        "HitBox-Head-P250": true,
    //        "HitBox-Chest-P250": true,
    //        "HitBox-Feet-P250": true,
    //        "Priority-P250": "Head",
    //        "PitchOffset-P250": -0.13,
    //        "TargetPredictSize-P250": 2.6,
    //        "FireMode-P250": "Packet",
    //        "FireTick-P250": 1,
    //        "NoSpreadBaseTick-P250": 4,
    //        "SilentRotate-P250": true,
    //        "HitBox-Head-Deagle": true,
    //        "HitBox-Chest-Deagle": true,
    //        "HitBox-Feet-Deagle": true,
    //        "Priority-Deagle": "Head",
    //        "PitchOffset-Deagle": 0.05,
    //        "TargetPredictSize-Deagle": 2.6,
    //        "FireMode-Deagle": "Packet",
    //        "FireTick-Deagle": 1,
    //        "NoSpreadBaseTick-Deagle": 1,
    //        "SilentRotate-Deagle": false,
    //        "HitBox-Head-MP7": true,
    //        "HitBox-Chest-MP7": true,
    //        "HitBox-Feet-MP7": true,
    //        "Priority-MP7": "Head",
    //        "PitchOffset-MP7": 0.79,
    //        "TargetPredictSize-MP7": 2.6,
    //        "FireMode-MP7": "Packet",
    //        "FireTick-MP7": 1,
    //        "NoSpreadBaseTick-MP7": 1,
    //        "SilentRotate-MP7": true
    //      }
    //    },

    //  "RageBot": {
    //      "state": false,
    //      "keybind": 19,
    //      "array": true,
    //      "autodisable": "NONE",
    //      "trigger": "TOGGLE",
    //      "values": {
    //        "PitchOffset": -0.13,
    //        "PosMode": "Mix",
    //        "PlayerPosMode": "Pos",
    //        "VelocityMode": "Pos",
    //        "ScriptEngineMode": "JavaScript",
    //        "CustomVelocityCode": "serverPos - lastTickPos",
    //        "CustomPosCode": "serverPos",
    //        "visibilitySensitivityTick": 0,
    //        "EnableAccumulation": true,
    //        "TargetEyeHeight": 0.8,
    //        "MaxRange": 70.0,
    //        "SilentRotate": true,
    //        "AutoRange": true,
    //        "AutoRange-CustomRange-AKRange": 45.08,
    //        "AutoRange-CustomRange-M4Range": 50.0,
    //        "AutoRange-CustomRange-AWPRange": 70.0,
    //        "AutoRange-CustomRange-MP7Range": 45.0,
    //        "AutoRange-CustomRange-P250Range": 35.0,
    //        "AutoRange-CustomRange-DeagleRange": 40.0,
    //        "AutoRange-CustomRange-ShotGunRange": 30.38,
    //        "TargetPredict": true,
    //        "TargetPredictSize": 2.6,
    //        "TargetPredictMaxVelocity": 1.09,
    //        "TargetPredictMinVelocity": 0.05,
    //        "PlayerPredict": false,
    //        "PlayerPredictSize": 2.49,
    //        "TargetVisibilityPredict": true,
    //        "TargetVisibilityPredictSize": 1.88,
    //        "PlayerVisibilityPredict": false,
    //        "PlayerVisibilityPredictSize": 1.5,
    //        "TargetVisibilityPredictFire": true,
    //        "FireMode": "Packet",
    //        "FireTick": 1,
    //        "NoSpread": true,
    //        "NoSpreadMode": "SwitchOffsets",
    //        "NoSpreadTriggerMode": "Tick",
    //        "noSpreadSwitchOffsetsPitchTick1": 0.04,
    //        "noSpreadSwitchOffsetsPitchTick2": 0.1,
    //        "noSpreadSwitchOffsetsYawTick1": -0.7,
    //        "noSpreadSwitchOffsetsYawTick2": 1.2,
    //        "NoSpreadBaseTick": 4,
    //        "AutoSneak": true,
    //        "AutoSneakMode": "Packet",
    //        "AutoSneakTriggerMode": "OnlyFire",
    //        "AutoSneakOnlyAwp": true,
    //        "FireLimit": true,
    //        "FireLimit-TargetVelocityY": -0.18,
    //        "FireLimit-TargetFallVelocity": -0.19,
    //        "FireLimit-TimeLimitedPrediction": true,
    //        "FireLimit-TimeLimitedPredictionTicks": 10,
    //        "FireLimit-MaxRandomRange": 20,
    //        "FireLimit-MainRandomRange": 1,
    //        "FireLimit-AwpOnly": true,
    //        "Jitter": false,
    //        "JitterYaw": true,
    //        "JitterPitch": false,
    //        "JitterFrequency": 1,
    //        "JitterAmplitude": 1,
    //        "HitBox": true,
    //        "HitBox-Head": true,
    //        "HitBox-Chest": true,
    //        "HitBox-Feet": true,
    //        "Priority": "Head",
    //        "BaseEyeHeightDetectionOffset": 0.02,
    //        "ChestDetectionOffset": 0.8,
    //        "FeetDetectionOffset": 0.0,
    //        "ChestAimOffset": 0.81,
    //        "FeetAimOffset": 0.0,
    //        "BoundingBoxDetection": true,
    //        "BoundingBoxDetectionMode": "20Points",
    //        "BoundingBoxDetectionSize": 0.04,
    //        "BoundingBoxOffsetY": 0.04,
    //        "BoundingBoxHeadOffsetY": 0.17,
    //        "BoundingBoxFeetOffsetY": 0.0,
    //        "DistanceOffset": true,
    //        "DistanceOffsetMultiplier": 0.45,
    //        "DistanceOffsetMaxRange": 75.0,
    //        "SneakYOffset": 0.08,
    //        "PlayerVecYVecYOffset": 0.23,
    //        "RayTraceBlocks-StopOnLiquid": true,
    //        "RayTraceBlocks-IgnoreBlockWithoutBoundingBox": true,
    //        "RayTraceBlocks-ReturnLastUncollidableBlock": false,
    //        "DelayControl": true,
    //        "DelayControlPrediction": true,
    //        "DelayControlPosMode": true,
    //        "DelayControlPosModeMinDelayValue": 82,
    //        "DelayControlPredictionModeMinDelayValue": 82,
    //        "Acceleration": false,
    //        "AccelerationLong": 2,
    //        "SpreadDebug": false,
    //        "AccelerationDebug": false,
    //        "VelocityDebug": false,
    //        "PosDebug": false,
    //        "TargetDebug": true,
    //        "TargetDebug-MaxHurtTime": 10,
    //        "TargetDebug-MinHurtTime": 10,
    //        "HitEffect": true,
    //        "HitEffectMode": "Lighting",
    //        "LightingSound": true,
    //        "Circle": true,
    //        "CircleRange": 70.84,
    //        "CircleRed": 255,
    //        "CircleGreen": 255,
    //        "CircleBlue": 255,
    //        "CircleAlpha": 255,
    //        "CircleThickness": 1.0,
    //        "Line": true,
    //        "LineYOffset": 1.46,
    //        "LineRed": 223,
    //        "LineGreen": 255,
    //        "LineBlue": 255,
    //        "LineAlpha": 255,
    //        "LineThickness": 1.0
    //      }
    //    },

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!whenRageBotIsIdle.get() || idle()) {
            if (rotateValue.get()) RotationUtils.setTargetRotation(Rotation(yawValue, pitchValue)) else {
                mc.thePlayer.rotationYaw = yawValue
                mc.thePlayer.rotationPitch = pitchValue
            }
            if (auto.get()) {
                yawValue = mc.thePlayer.rotationYaw + yawOffset.get()
                pitchValue = pitch.get()
            } else {
                yawValue = yaw.get()
                pitchValue = pitch.get()
            }
            if (sneakMode.get() == "Packet") mc.netHandler.addToSendQueue(
                C0BPacketEntityAction(
                    mc.thePlayer,
                    C0BPacketEntityAction.Action.START_SNEAKING
                )
            )
            else if (sneakMode.get() == "Legit") mc.thePlayer.isSneaking = true
            else mc.netHandler.addToSendQueue(
                C0BPacketEntityAction(
                    mc.thePlayer,
                    C0BPacketEntityAction.Action.STOP_SNEAKING
                )
            )
            if (dynamic.get()) if (rotationTick < 10) rotationTick++ else {
                yawValue += dynamicAmplitude.get()
                rotationTick = 0
            }
        }
    }
}