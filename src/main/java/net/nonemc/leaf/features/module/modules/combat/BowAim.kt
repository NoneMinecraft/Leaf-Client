package net.nonemc.leaf.features.module.modules.combat

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBow
import net.nonemc.leaf.libs.data.Rotation
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.Render3DEvent
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.features.module.Module
import net.nonemc.leaf.features.module.ModuleCategory
import net.nonemc.leaf.features.module.ModuleInfo
import net.nonemc.leaf.libs.entity.EntityTypeLib
import net.nonemc.leaf.libs.rotation.RotationBaseLib
import net.nonemc.leaf.libs.render.RenderUtils
import net.nonemc.leaf.value.BoolValue
import net.nonemc.leaf.value.FloatValue
import net.nonemc.leaf.value.ListValue
import java.awt.Color
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt

@ModuleInfo(name = "BowAim", category = ModuleCategory.COMBAT)
class BowAim : Module() {
    private val silentValue = BoolValue("Silent", true)
    private val predictValue = BoolValue("Predict", true)
    private val throughWallsValue = BoolValue("ThroughWalls", false)
    private val predictSizeValue = FloatValue("PredictSize", 2F, 0.1F, 5F)
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction"), "Direction")
    private val markValue = BoolValue("Mark", true)

    private var target: Entity? = null

    override fun onDisable() {
        target = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        target = null
        if (mc.thePlayer.itemInUse?.item is ItemBow) {
            val entity = getTarget(throughWallsValue.get(), priorityValue.get()) ?: return

            target = entity
            val player = mc.thePlayer

            val posX =
                target!!.posX + (if (predictValue.get()) (target!!.posX - target!!.prevPosX) * predictSizeValue.get() else 0.0) - (player.posX + (if (predictValue.get()) (player.posX - player.prevPosX) else 0.0))
            val posY =
                target!!.entityBoundingBox.minY + (if (predictValue.get()) (target!!.entityBoundingBox.minY - target!!.prevPosY) * predictSizeValue.get() else 0.0) + target!!.eyeHeight - 0.15 - (player.entityBoundingBox.minY + (if (predictValue.get()) (player.posY - player.prevPosY) else 0.0)) - player.getEyeHeight()
            val posZ =
                target!!.posZ + (if (predictValue.get()) (target!!.posZ - target!!.prevPosZ) * predictSizeValue.get() else 0.0) - (player.posZ + (if (predictValue.get()) (player.posZ - player.prevPosZ) else 0.0))
            val posSqrt = sqrt(posX * posX + posZ * posZ)

            var velocity = player.itemInUseDuration / 20f
            velocity = (velocity * velocity + velocity * 2) / 3

            if (velocity > 1) velocity = 1f

            val rotation = Rotation(
                (atan2(posZ, posX) * 180 / Math.PI).toFloat() - 90,
                -Math.toDegrees(atan((velocity * velocity - sqrt(velocity * velocity * velocity * velocity - 0.006f * (0.006f * (posSqrt * posSqrt) + 2 * posY * (velocity * velocity)))) / (0.006f * posSqrt)))
                    .toFloat()
            )

            if (silentValue.get()) RotationBaseLib.setTargetRotation(rotation)
            else {
                mc.thePlayer.rotationYaw = rotation.yaw
                mc.thePlayer.rotationPitch = rotation.pitch
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (target != null && !priorityValue.equals("Multi") && markValue.get()) {
            RenderUtils.drawPlatform(target, Color(37, 126, 255, 70))
        }
    }

    private fun getTarget(throughWalls: Boolean, priorityMode: String): Entity? {
        val targets = mc.theWorld.loadedEntityList.filter {
            it is EntityLivingBase && EntityTypeLib.isSelected(it, true) &&
                    (throughWalls || mc.thePlayer.canEntityBeSeen(it))
        }

        return when (priorityMode.uppercase()) {
            "DISTANCE" -> targets.minByOrNull { mc.thePlayer.getDistanceToEntity(it) }
            "DIRECTION" -> targets.minByOrNull { RotationBaseLib.getRotationDifference(it) }
            "HEALTH" -> targets.minByOrNull { (it as EntityLivingBase).health }
            else -> null
        }
    }
}