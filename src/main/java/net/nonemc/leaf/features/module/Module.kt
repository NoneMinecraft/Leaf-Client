
package net.nonemc.leaf.features.module

import net.nonemc.leaf.Leaf
import net.nonemc.leaf.event.Listenable
import net.nonemc.leaf.features.module.modules.client.HUD
import net.nonemc.leaf.features.module.modules.client.SoundModule
import net.nonemc.leaf.ui.client.hud.element.elements.Notification
import net.nonemc.leaf.ui.client.hud.element.elements.NotifyType
import net.nonemc.leaf.ui.i18n.LanguageManager
import net.nonemc.leaf.utils.AnimationHelper
import net.nonemc.leaf.utils.ClassUtils
import net.nonemc.leaf.utils.ClientUtils
import net.nonemc.leaf.utils.MinecraftInstance
import net.nonemc.leaf.utils.render.Animation
import net.nonemc.leaf.utils.render.ColorUtils.stripColor
import net.nonemc.leaf.utils.render.EaseUtils
import net.nonemc.leaf.utils.render.Translate
import net.nonemc.leaf.value.Value
import org.lwjgl.input.Keyboard

open class Module : MinecraftInstance(), Listenable {
    val translate = Translate(0F,0F)
    val tab = Translate(0f , 0f)
    var expanded: Boolean = false
    val animation: AnimationHelper
    var name: String
    var localizedName = ""
        get() = field.ifEmpty { name }
    var description: String
    var category: ModuleCategory
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {
            field = keyBind

            if (!Leaf.isStarting) {
                Leaf.configManager.smartSave()
            }
        }
    var array = true
        set(array) {
            field = array

            if (!Leaf.isStarting) {
                Leaf.configManager.smartSave()
            }
        }
    val canEnable: Boolean
    var autoDisable: EnumAutoDisableType
    var triggerType: EnumTriggerType
    val moduleCommand: Boolean
    val moduleInfo = javaClass.getAnnotation(ModuleInfo::class.java)!!
    var splicedName = ""
        get() {
            if (field.isEmpty()) {
                val sb = StringBuilder()
                val arr = name.toCharArray()
                for (i in arr.indices) {
                    val char = arr[i]
                    if (i != 0 && !Character.isLowerCase(char) && Character.isLowerCase(arr[i - 1])) {
                        sb.append(' ')
                    }
                    sb.append(char)
                }
                field = sb.toString()
            }
            return field
        }

    init {
        name = moduleInfo.name
        animation = AnimationHelper(this)
        description = "%module.$name.description%"
        category = moduleInfo.category
        keyBind = moduleInfo.keyBind
        array = moduleInfo.array
        canEnable = moduleInfo.canEnable
        autoDisable = moduleInfo.autoDisable
        moduleCommand = moduleInfo.moduleCommand
        triggerType = moduleInfo.triggerType
    }

    open fun onLoad() {
        localizedName = if(LanguageManager.getAndFormat("module.$name.name") == "module.$name.name") {
            name
        } else {
            LanguageManager.getAndFormat("module.$name.name")
        }
    }

    // Current state of module
    var state = false
        set(value) {
            if (field == value) return

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!Leaf.isStarting) {
                if (value) {
                    SoundModule.playSound(true)
                    Leaf.hud.addNotification(Notification(LanguageManager.getAndFormat("notify.module.title"), LanguageManager.getAndFormat("notify.module.enable", localizedName), NotifyType.SUCCESS))
                } else {
                    SoundModule.playSound(false)
                    Leaf.hud.addNotification(Notification("%notify.module.title%", LanguageManager.getAndFormat("notify.module.disable", localizedName), NotifyType.ERROR))
                }
            }

            // Call on enabled or disabled
            try {
                field = canEnable && value
                if (value) {
                    onEnable()
                } else {
                    onDisable()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            // Save module state
            Leaf.configManager.smartSave()
        }

    // HUD
    val hue = Math.random().toFloat()
    var slideAnimation: Animation? = null
    var slide = 0f
        get() {
            if (slideAnimation != null) {
                field = slideAnimation!!.value.toFloat()
                if (slideAnimation!!.state == Animation.EnumAnimationState.STOPPED) {
                    slideAnimation = null
                }
            }
            return field
        }
        set(value) {
            if (slideAnimation == null || (slideAnimation != null && slideAnimation!!.to != value.toDouble())) {
                slideAnimation = Animation(EaseUtils.EnumEasingType.valueOf(HUD.arraylistXAxisAnimTypeValue.get()), EaseUtils.EnumEasingOrder.valueOf(HUD.arraylistXAxisAnimOrderValue.get()), field.toDouble(), value.toDouble(), HUD.arraylistXAxisAnimSpeedValue.get() * 30L).start()
            }
        }
    var yPosAnimation: Animation? = null
    var yPos = 0f
        get() {
            if (yPosAnimation != null) {
                field = yPosAnimation!!.value.toFloat()
                if (yPosAnimation!!.state == Animation.EnumAnimationState.STOPPED) {
                    yPosAnimation = null
                }
            }
            return field
        }
        set(value) {
            if (yPosAnimation == null || (yPosAnimation != null && yPosAnimation!!.to != value.toDouble())) {
                yPosAnimation = Animation(EaseUtils.EnumEasingType.valueOf(HUD.arraylistYAxisAnimTypeValue.get()), EaseUtils.EnumEasingOrder.valueOf(HUD.arraylistYAxisAnimOrderValue.get()), field.toDouble(), value.toDouble(), HUD.arraylistYAxisAnimSpeedValue.get() * 30L).start()
            }
        }

    // Tag
    open val tag: String?
        get() = null

    val tagName: String
        get() = "$name${if (tag == null) "" else " §7$tag"}"

    val colorlessTagName: String
        get() = "$name${if (tag == null) "" else " " + stripColor(tag!!)}"

    var width = 10

    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Print [msg] to chat as alert
     */
    protected fun alert(msg: String) = ClientUtils.displayAlert(msg)

    /**
     * Print [msg] to chat as plain text
     */
    protected fun chat(msg: String) = ClientUtils.displayChatMessage(msg)

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {}

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Called when module initialized
     */
    open fun onInitialize() {}

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = ClassUtils.getValues(this.javaClass, this)

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = values.find { it.name.equals(valueName, ignoreCase = true) }

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
}