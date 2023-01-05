package net.swordcraft.server.utils

import java.util.*

fun ByteArray.encodeBase64(): String {
    return String(Base64.getEncoder().encode(this))
}