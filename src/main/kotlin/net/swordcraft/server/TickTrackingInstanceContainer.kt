package net.swordcraft.server

import net.minestom.server.instance.IChunkLoader
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.world.DimensionType
import java.util.UUID

class TickTrackingInstanceContainer(
    uuid: UUID,
    type: DimensionType,
) : InstanceContainer(uuid, type) {

    constructor(uuid: UUID) : this(uuid, DimensionType.OVERWORLD)
    constructor() : this(UUID.randomUUID(), DimensionType.OVERWORLD)

    var tickCount: Long = 0
        private set

    override fun tick(time: Long) {
        tickCount++
        super.tick(time)
    }

}