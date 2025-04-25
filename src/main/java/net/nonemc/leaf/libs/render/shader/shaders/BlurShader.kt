package net.nonemc.leaf.libs.render.shader.shaders

import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.shader.Framebuffer
import net.nonemc.leaf.libs.render.GLUtils
import net.nonemc.leaf.libs.render.shader.Shader
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20

object BlurShader : Shader("blur.frag") {
    override fun setupUniforms() {
        setupUniform("radius")
        setupUniform("direction")
        setupUniform("texture")
        setupUniform("texelsize")
        setupUniform("weights")
    }

    override fun updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0)
        GL20.glUniform2f(getUniform("texelsize"), 1f / mc.displayWidth, 1f / mc.displayHeight)
    }
}