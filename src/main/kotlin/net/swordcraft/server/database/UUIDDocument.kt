package net.swordcraft.server.database

import org.bson.Document
import java.util.UUID

/**
 * Because I am lazy and don't want to write this every time.
 */
class UUIDDocument(uuid: UUID) : Document() {
    init {
        this["uuid"] = uuid.toString()
    }
}