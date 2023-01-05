package net.swordcraft.server.utils

import net.swordcraft.server.Tachyon
import java.io.File

fun File.createIfNotExists() {
    if (!exists()) {
        createNewFile()
    }
}

fun File.checkResource(resource: String, overwrite: Boolean = false): File {
    if (!exists() || overwrite) {
        createNewFile()
        val stream = Tachyon.javaClass.classLoader.getResourceAsStream(resource)
            ?: throw IllegalArgumentException("Resource does not exist: $resource")
        writeBytes(stream.readAllBytes())
    }
    return this
}