package net.nonemc.leaf.ui.mainmenu.thread

import java.util.concurrent.Executors

object AIBackGroundThread {
    private val executor = Executors.newSingleThreadExecutor()
    fun run(task: () -> Unit) {
        executor.execute {
            task()
        }
    }
}
