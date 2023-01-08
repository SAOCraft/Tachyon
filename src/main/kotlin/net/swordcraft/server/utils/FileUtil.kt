package net.swordcraft.server.utils

import net.swordcraft.server.Tachyon
import java.io.File

fun File.createIfNotExists(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
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

fun File.createFolderIfNotExists(): File {
    if (!exists()) {
        mkdirs()
    }
    return this
}