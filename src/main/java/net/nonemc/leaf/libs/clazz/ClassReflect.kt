package net.nonemc.leaf.libs.clazz

import net.nonemc.leaf.value.Value
import org.apache.logging.log4j.core.config.plugins.ResolverUtil
import java.lang.reflect.Modifier

object ClassReflect {
    fun getObjectInstance(clazz: Class<*>): Any {
        clazz.declaredFields.forEach {
            if (it.name.equals("INSTANCE")) return it.get(null)
        }
        throw IllegalAccessException("This class not a kotlin object")
    }
    fun getValues(clazz: Class<*>, instance: Any) = clazz.declaredFields.map { valueField ->
        valueField.isAccessible = true
        valueField[instance]}.filterIsInstance<Value<*>>()
    fun <T : Any> resolvePackage(packagePath: String, clazz: Class<T>): List<Class<out T>> {
        val resolver = ResolverUtil()
        resolver.classLoader = clazz.classLoader
        resolver.findInPackage(object : ResolverUtil.ClassTest() {override fun matches(type: Class<*>): Boolean { return true }},packagePath)
        val list = mutableListOf<Class<out T>>()
        for(resolved in resolver.classes) {
            resolved.declaredMethods.find {
                Modifier.isNative(it.modifiers)
            }?.let {
                val klass1 = it.declaringClass.typeName+"."+it.name
                throw UnsatisfiedLinkError(klass1+"\n\tat ${klass1}(Native Method)")
            }
            if(clazz.isAssignableFrom(resolved) && !resolved.isInterface && !Modifier.isAbstract(resolved.modifiers)) list.add(resolved as Class<out T>)
        }
        return list
    }
}