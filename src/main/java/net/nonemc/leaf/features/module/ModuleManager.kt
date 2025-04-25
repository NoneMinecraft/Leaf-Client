package net.nonemc.leaf.features.module

import net.minecraft.client.Minecraft
import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.EventTarget
import net.nonemc.leaf.event.KeyEvent
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.event.UpdateEvent
import net.nonemc.leaf.ui.hud.element.elements.Notification
import net.nonemc.leaf.ui.hud.element.elements.NotifyType
import net.nonemc.leaf.libs.clazz.ClassReflect
import org.lwjgl.input.Keyboard

class ModuleManager : Listenable {
    val modules = mutableListOf<Module>()
    private val moduleClassMap = hashMapOf<Class<*>, Module>()
    fun getModuleInCategory(category: ModuleCategory) = modules.filter { it.category == category }
    var pendingBindModule: Module? = null

    init {
        Leaf.eventManager.registerListener(this)
    }

    fun registerModules() {
        ClassReflect.resolvePackage("${this.javaClass.`package`.name}.modules", Module::class.java)
            .forEach(this::registerModule)

        modules.forEach { it.onInitialize() }
        modules.forEach { it.onLoad() }
    }
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module
        modules.sortBy { it.name }

        generateCommand(module)

        Leaf.eventManager.registerListener(module)
    }

    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: IllegalAccessException) {
            registerModule(ClassReflect.getObjectInstance(moduleClass) as Module)
        } catch (e: Throwable) {
            println("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        Leaf.eventManager.unregisterListener(module)
    }

    internal fun generateCommand(module: Module) {
        if (!module.moduleCommand) {
            return
        }

        val values = module.values

        if (values.isEmpty()) {
            return
        }

        Leaf.commandManager.registerCommand(ModuleCommand(module, values))
    }

    fun <T : Module> getModule(moduleClass: Class<T>): T? {
        return moduleClassMap[moduleClass] as T?
    }

    operator fun <T : Module> get(clazz: Class<T>) = getModule(clazz)

    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    fun getKeyBind(key: Int) = modules.filter { it.keyBind == key }

    @EventTarget
    private fun onKey(event: KeyEvent) {
        if (pendingBindModule == null) {
            modules.toMutableList().filter { it.triggerType == EnumTriggerType.TOGGLE && it.keyBind == event.key }
                .forEach { it.toggle() }
        } else {
            pendingBindModule!!.keyBind = event.key
            Leaf.hud.addNotification(
                Notification(
                    "KeyBind",
                    "Bound ${pendingBindModule!!.name} to ${Keyboard.getKeyName(event.key)}.",
                    NotifyType.INFO
                )
            )
            pendingBindModule = null
        }
    }

    @EventTarget
    private fun onUpdate(event: UpdateEvent) {
        if (pendingBindModule != null || Minecraft.getMinecraft().currentScreen != null) {
            return
        }
        modules.filter { it.triggerType == EnumTriggerType.PRESS }.forEach { it.state = Keyboard.isKeyDown(it.keyBind) }
    }

    override fun handleEvents() = true
}