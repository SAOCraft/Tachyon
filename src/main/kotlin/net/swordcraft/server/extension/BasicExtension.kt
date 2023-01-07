package net.swordcraft.server.extension

import dev.dejvokep.boostedyaml.YamlDocument
import net.minestom.server.extensions.Extension
import net.swordcraft.server.Tachyon
import net.swordcraft.server.utils.checkResource
import java.io.File

open class BasicExtension : Extension() {

    val dataDir = File("${Tachyon.serverPath}/extensions/${this.origin.name}").apply { if (!exists()) mkdirs() }
    var config: YamlDocument? = null

    override fun initialize() {
        logger.info("Loaded ${this.origin.name} extension")
    }

    override fun terminate() {
    }

    protected fun saveDefaultConfig(overwrite: Boolean = false) {
        val config = File("${dataDir.absolutePath}/config.yml")
        if (!config.exists()) {
            config.checkResource("config.yml", overwrite)
        }
    }

}