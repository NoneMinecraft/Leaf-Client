package net.nonemc.leaf.script.remapper.injection.transformers.handlers

import net.nonemc.leaf.script.ScriptSafetyManager
import net.nonemc.leaf.script.remapper.Remapper
import org.objectweb.asm.Type
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Method

object AbstractJavaLinkerHandler {
    //!
    @JvmStatic
    fun addMember(clazz: Class<*>, name: String, accessibleObject: AccessibleObject): String {
        if (accessibleObject !is Method) {
            return name
        }

        var currentClass = clazz
        while (currentClass.name != "java.lang.Object") {
            if (ScriptSafetyManager.isRestrictedSimple(currentClass, name)) {
                return "RESTRICTED"
            }
            val remapped = Remapper.remapMethod(currentClass, name, Type.getMethodDescriptor(accessibleObject))

            if (remapped != name) {
                return remapped
            }

            if (currentClass.superclass == null) {
                break
            }

            currentClass = currentClass.superclass
        }

        return name
    }

    @JvmStatic
    fun addMember(clazz: Class<*>, name: String): String {
        var currentClass = clazz
        while (currentClass.name != "java.lang.Object") {
            if (ScriptSafetyManager.isRestrictedSimple(currentClass, name)) {
                return "RESTRICTED"
            }
            val remapped = Remapper.remapField(currentClass, name)

            if (remapped != name) {
                return remapped
            }

            if (currentClass.superclass == null) {
                break
            }

            currentClass = currentClass.superclass
        }

        return name
    }

    @JvmStatic
    fun setPropertyGetter(clazz: Class<*>, name: String): String {
        var currentClass = clazz
        while (currentClass.name != "java.lang.Object") {
            if (ScriptSafetyManager.isRestrictedSimple(currentClass, name)) {
                return "RESTRICTED"
            }
            val remapped = Remapper.remapField(currentClass, name)

            if (remapped != name) {
                return remapped
            }

            if (currentClass.superclass == null) {
                break
            }

            currentClass = currentClass.superclass
        }

        return name
    }
}