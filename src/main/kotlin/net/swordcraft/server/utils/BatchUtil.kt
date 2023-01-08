package net.swordcraft.server.utils

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.batch.ChunkBatch
import java.lang.reflect.Field

@Suppress("UNCHECKED_CAST")
fun AbsoluteBlockBatch.getAffectedChunks(): LongArray {
    val field: Field = this.javaClass.getDeclaredField("chunkBatchesMap")
    val accessible: Boolean = field.canAccess(this)
    field.isAccessible = true
    val chunkBatchesMap: Long2ObjectMap<ChunkBatch> = field.get(this) as Long2ObjectMap<ChunkBatch>
    field.isAccessible = accessible
    return chunkBatchesMap.keys.toLongArray()
}