package net.nonemc.leaf.utils.misc

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList

object ASMUtils {
    fun toClassNode(bytes: ByteArray): ClassNode {
        val classReader = ClassReader(bytes)
        val classNode = ClassNode()
        classReader.accept(classNode, 0)

        return classNode
    }

    fun toBytes(classNode: ClassNode): ByteArray {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        classNode.accept(classWriter)

        return classWriter.toByteArray()
    }

    fun toNodes(vararg nodes: AbstractInsnNode): InsnList {
        val insnList = InsnList()
        for (node in nodes)
            insnList.add(node)
        return insnList
    }
}