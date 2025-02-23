package net.nonemc.leaf.event

import net.nonemc.leaf.utils.MinecraftInstance

class EventManager : MinecraftInstance() {
    private val registry = HashMap<Class<out Event>, MutableList<EventHook>>()
    fun registerListener(listener: Listenable) {
        for (method in listener.javaClass.declaredMethods) {
            if (method.isAnnotationPresent(EventTarget::class.java) && method.parameterTypes.size == 1) {
                try {
                    if (!method.isAccessible) {
                        method.isAccessible = true
                    }

                    val eventClass = method.parameterTypes[0] as Class<out Event>
                    val eventTarget = method.getAnnotation(EventTarget::class.java)

                    val invokableEventTargets = registry.getOrPut(eventClass) { mutableListOf() }
                    invokableEventTargets.add(EventHook(listener, method, eventTarget))
                    registry[eventClass] = invokableEventTargets
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
    }
    fun unregisterListener(listenable: Listenable) {
        for ((key, targets) in registry) {
            targets.removeIf { it.eventClass == listenable }

            registry[key] = targets
        }
    }
    fun callEvent(event: Event) {
        val targets = registry[event.javaClass] ?: return
        try {
            for (invokableEventTarget in targets) {
                try {
                    if (!invokableEventTarget.eventClass.handleEvents() && !invokableEventTarget.isIgnoreCondition) {
                        continue
                    }

                    invokableEventTarget.method.invoke(invokableEventTarget.eventClass, event)
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        }catch (e :Exception){
            e.printStackTrace();
        }
    }
}
