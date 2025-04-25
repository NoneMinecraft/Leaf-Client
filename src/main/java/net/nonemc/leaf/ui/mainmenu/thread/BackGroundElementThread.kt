package net.nonemc.leaf.ui.mainmenu.thread

import java.util.concurrent.Executors

object BackGroundElementThread {
    private val executor = Executors.newSingleThreadExecutor()
    fun run(task: () -> Unit) {
        executor.execute {
            task()
        }
    }
}
