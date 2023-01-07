package net.swordcraft.server.schematic

enum class Rotation {

    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    CLOCKWISE_270;

    fun rotate(rotation: Rotation): Rotation {
        return values()[(ordinal + rotation.ordinal) % 4]
    }

    companion object {
        /* Converts Minestom's Rotation enum to this one */
        fun from(rotation: net.minestom.server.utils.Rotation): Rotation {
            return values()[rotation.ordinal / 2]
        }
    }


}