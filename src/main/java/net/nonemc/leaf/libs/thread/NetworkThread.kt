package net.nonemc.leaf.libs.thread

import java.util.concurrent.Executors

object NetworkThread {
    private val executor = Executors.newSingleThreadExecutor()
    fun run(task: () -> Unit) {
        executor.execute {
            task()
        }
    }
}