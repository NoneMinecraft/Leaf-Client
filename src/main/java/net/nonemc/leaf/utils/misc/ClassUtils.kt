﻿package net.nonemc.leaf.utils.misc

import net.nonemc.leaf.value.Value
import org.apache.logging.log4j.core.config.plugins.ResolverUtil
import java.lang.reflect.Modifier

object ClassUtils {

    fun getObjectInstance(clazz: Class<*>): Any {
        clazz.declaredFields.forEach {
            if (it.name.equals("INSTANCE")) {
                return it.get(null)
            }
        }
        throw IllegalAccessException("This class not a kotlin object")
    }

    fun getValues(clazz: Class<*>, instance: Any) = clazz.declaredFields.map { valueField ->
        valueField.isAccessible = true
        valueField[instance]
    }.filterIsInstance<Value<*>>()

    /**
     * scan classes with specified superclass like what Reflections do but with log4j [ResolverUtil]
     * @author liulihaocai
     */
    fun <T : Any> resolvePackage(packagePath: String, klass: Class<T>): List<Class<out T>> {
        // use resolver in log4j to scan classes in target package
        val resolver = ResolverUtil()

        // set class loader
        resolver.classLoader = klass.classLoader

        // set package to scan
        resolver.findInPackage(object : ResolverUtil.ClassTest() {
            override fun matches(type: Class<*>): Boolean {
                return true
            }
        }, packagePath)

        // use a list to cache classes
        val list = mutableListOf<Class<out T>>()

        for (resolved in resolver.classes) {
            resolved.declaredMethods.find {
                Modifier.isNative(it.modifiers)
            }?.let {
                val klass1 = it.declaringClass.typeName + "." + it.name
                throw UnsatisfiedLinkError(klass1 + "\n\tat ${klass1}(Native Method)") // we don't want native methods
            }
            // check if class is assignable from target class
            if (klass.isAssignableFrom(resolved) && !resolved.isInterface && !Modifier.isAbstract(resolved.modifiers)) {
                // add to list
                list.add(resolved as Class<out T>)
            }
        }

        return list
    }
}