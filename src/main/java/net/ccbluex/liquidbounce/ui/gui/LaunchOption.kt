
package net.ccbluex.liquidbounce.ui.gui

/**
 * used at initialize something dynamic
 */
abstract class LaunchOption {
    /**
     * called when startClient
     */
    open fun start() {}

    /**
     * called when stopClient
     */
    open fun stop() {}
}