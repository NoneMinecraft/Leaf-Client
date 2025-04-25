package net.nonemc.leaf.libs.data

import net.minecraft.util.Vec3
import net.nonemc.leaf.features.module.modules.world.PlaceInfo

data class Rotation(var yaw: Float, var pitch: Float)
data class VecRotation(val vec: Vec3, val rotation: Rotation)
data class PlaceRotation(val placeInfo: PlaceInfo, val rotation: Rotation)