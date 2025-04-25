﻿package net.nonemc.leaf.libs.extensions

import java.awt.Color

fun Color.darker(factor: Float) = Color(
    this.red / 255F * factor.coerceIn(0F, 1F),
    this.green / 255F * factor.coerceIn(0F, 1F),
    this.blue / 255F * factor.coerceIn(0F, 1F),
    this.alpha / 255F
)