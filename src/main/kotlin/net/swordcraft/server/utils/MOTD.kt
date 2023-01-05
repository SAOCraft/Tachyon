package net.swordcraft.server.utils

import net.kyori.adventure.text.Component
import net.minestom.server.ping.ResponseData
import net.swordcraft.server.Tachyon
import java.io.File

class MOTD : ResponseData() {

    init {
        setPlayersHidden(false)
        maxPlayer = Tachyon.config.getInt("server.max-players", 20)
        val motd = Tachyon.config.getString("server.motd", "<gray>A SwordCraft Server").color()
        description = motd
        //this.favicon = "data:image/png;base64,$faviconBase64"
    }

    companion object {
        private val faviconFile = File(Tachyon.serverPath, "favicon.png").checkResource("favicon.png")
        private val faviconBase64 = faviconFile.inputStream().readBytes().encodeBase64()
        val unsupportedPing = ResponseData().apply {
            setPlayersHidden(true)
            description = Component.text("Unsupported connection... Sorry D;")
        }
    }

}