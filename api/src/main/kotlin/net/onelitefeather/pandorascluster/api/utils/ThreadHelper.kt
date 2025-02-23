package net.onelitefeather.pandorascluster.api.utils

interface ThreadHelper {
    fun syncThreadForServiceLoader(runnable: Runnable) {
        val currentThread = Thread.currentThread()
        val originalClassLoader = currentThread.contextClassLoader
        val pluginClassLoader = this.javaClass.classLoader
        try {
            currentThread.contextClassLoader = pluginClassLoader
            runnable.run()
        } finally {
            currentThread.contextClassLoader = originalClassLoader
        }
    }
}