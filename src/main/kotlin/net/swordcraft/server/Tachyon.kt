package net.swordcraft.server

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.io.File
import java.net.URLDecoder
import java.util.UUID

import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.ping.ServerListPingType
import net.swordcraft.server.database.DatabaseStorage
import net.swordcraft.server.utils.MOTD
import net.swordcraft.server.utils.checkResource
import java.nio.charset.StandardCharsets
import java.time.Duration

object Tachyon {

    private val localPath: String = URLDecoder.decode(Tachyon::class.java.protectionDomain.codeSource.location.path, StandardCharsets.UTF_8)
    val serverPath: String = localPath.substring(0, localPath.lastIndexOf("/"))

    val server: MinecraftServer = MinecraftServer.init()

    val config: YamlDocument = createInternalConfig("config.yml", "config.yml")
    val messages: YamlDocument = createInternalConfig("messages.yml", "messages.yml")

    lateinit var miniMessage: MiniMessage
    lateinit var world: InstanceContainer

    val database: DatabaseStorage = DatabaseStorage(
        config.getString("database.name", "data"),
        config.getString("database.url", "mongodb://localhost:27017"),
    )

    @JvmStatic
    private val users: MutableSet<UUID> = HashSet()

    @JvmStatic
    fun main(args: Array<String>) {
        buildMiniMessage()
        registerInternalListeners()
        checkOnlineMode()
        MinecraftServer.setBrandName(config.getString("server.brand", "SwordCraft"))
        val address = config.getString("server.address", "0.0.0.0")
        val port = config.getInt("server.port", 25565)
        server.start(address, port)
        MinecraftServer.LOGGER.info("Using binds: ${address}:${port}")
    }

    @JvmStatic
    private fun checkOnlineMode() {
        if (config.getBoolean("server.online-mode", true)) {
            MojangAuth.init()
            MinecraftServer.LOGGER.info("Server is running in online (secure) mode.")
        }
        else {
            MinecraftServer.LOGGER.warn("Server is running in offline (insecure) mode.")
        }
    }

    @JvmStatic
    private fun registerInternalListeners() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent::class.java) {
            it.setSpawningInstance(world)
        }
        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent::class.java) { it.responseData = if (it.pingType != ServerListPingType.MODERN_FULL_RGB) MOTD.unsupportedPing else MOTD() }
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent::class.java) {
            if (config.getInt("server.reconnect-delay") == 0) {
                if (users.contains(it.player.uuid)) {
                    it.player.kick(messages.getString("reconnect-delay-message"))
                }
                else {
                    users.add(it.player.uuid)
                    MinecraftServer.getSchedulerManager().buildTask { users.remove(it.player.uuid) }
                        .delay(Duration.ofSeconds(config.getLong("server.reconnect-delay", 5)))
                        .schedule()
                }
            }
        }
    }

    @JvmStatic
    private fun buildMiniMessage() {
        val messages = Tachyon.messages
        val tags = mutableListOf<TagResolver>()
        messages.keys.forEach {
            val msg = messages[it as String]
            if (msg is String)
                tags.add(TagResolver.resolver(it, Tag.inserting(Component.text(msg))))
        }
        Tachyon.miniMessage = MiniMessage.builder().tags(
            TagResolver.builder()
                .resolver(TagResolver.standard())
                .resolvers(tags)
                .build()
        )
            .build()
    }

    @JvmStatic
    private fun createInternalConfig(path: String, resource: String): YamlDocument {
        val file = File("$serverPath/${path}").checkResource(resource)
        return YamlDocument.create(
            file, GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
            DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(BasicVersioning("file-version")).build()
        )
    }


}