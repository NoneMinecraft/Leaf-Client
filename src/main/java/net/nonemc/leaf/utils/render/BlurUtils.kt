﻿package net.nonemc.leaf.utils.render

import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.util.ResourceLocation
import net.nonemc.leaf.injection.access.StaticStorage
import net.nonemc.leaf.utils.MinecraftInstance

object BlurUtils : MinecraftInstance() {
    private val blurShader: ShaderGroup = ShaderGroup(
        mc.textureManager,
        mc.resourceManager,
        mc.framebuffer,
        ResourceLocation("shaders/post/blurArea.json")
    )
    private lateinit var buffer: Framebuffer
    private var lastScale = 0
    private var lastScaleWidth = 0
    private var lastScaleHeight = 0

    private fun reinitShader() {
        blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight)
        buffer = Framebuffer(mc.displayWidth, mc.displayHeight, true)
        buffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f)
    }

    fun draw(x: Float, y: Float, width: Float, height: Float, radius: Float) {
        val scale = StaticStorage.scaledResolution
        val factor = scale.scaleFactor
        val factor2 = scale.scaledWidth
        val factor3 = scale.scaledHeight
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3) {
            reinitShader()
        }
        lastScale = factor
        lastScaleWidth = factor2
        lastScaleHeight = factor3
        blurShader.listShaders[0].shaderManager.getShaderUniform("BlurXY")[x] = factor3 - y - height
        blurShader.listShaders[1].shaderManager.getShaderUniform("BlurXY")[x] = factor3 - y - height
        blurShader.listShaders[0].shaderManager.getShaderUniform("BlurCoord")[width] = height
        blurShader.listShaders[1].shaderManager.getShaderUniform("BlurCoord")[width] = height
        blurShader.listShaders[0].shaderManager.getShaderUniform("Radius").set(radius)
        blurShader.listShaders[1].shaderManager.getShaderUniform("Radius").set(radius)
        blurShader.loadShaderGroup(mc.timer.renderPartialTicks)
        mc.framebuffer.bindFramebuffer(true)
    }
}