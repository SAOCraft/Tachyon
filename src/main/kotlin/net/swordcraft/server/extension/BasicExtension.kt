package net.swordcraft.server.extension

import dev.dejvokep.boostedyaml.YamlDocument
import net.minestom.server.extensions.Extension
import net.swordcraft.server.Tachyon
import net.swordcraft.server.utils.checkResource
import java.io.File

open class BasicExtension : Extension() {

    val dataDir = File("${Tachyon.serverPath}/extensions/${this.origin.name}").apply { if (!exists()) mkdirs() }
    lateinit var config: YamlDocument

    override fun initialize() {
        this::class.java.getResourceAsStream("config.yml")
            ?: throw Exception("Config file not found... unloading extension ${this.origin.name}")
        val configFile = File(dataDir, "config.yml").checkResource("config.yml")
        config = YamlDocument.create(configFile)
        logger.info("Loaded ${this.origin.name} extension")
    }

    override fun terminate() {
    }

}