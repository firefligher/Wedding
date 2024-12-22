package dev.fir3.iwan.io.common

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object ClassLoaderUtilities {
    @Throws(IOException::class)
    fun queryClasses(
        classLoader: ClassLoader,
        packageName: String
    ): Set<Class<*>> = classLoader.getResourceAsStream(
        packageName.replace('.', '/')
    )?.let { stream ->
        BufferedReader(InputStreamReader(stream)).use { reader ->
            val classes = mutableSetOf<Class<*>>()
            var line: String?

            while (true) {
                line = reader.readLine()

                if (line == null) break
                if (!line.endsWith(".class")) continue

                val discoveredClass = classLoader.loadClass(
                    "${packageName}.${line.substringBeforeLast('.')}"
                )

                classes.add(discoveredClass)
            }

            classes
        }
    } ?: throw IOException("Cannot read package elements")
}
