package net.swordcraft.server.schematic

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Utils
import java.nio.ByteBuffer

class SchematicBuilder {

    private val blockSet: MutableMap<Point, Block> = HashMap()
    var offset = Vec.ZERO

    fun addBlock(point: Point, block: Block) {
        blockSet[point] = block
    }

    fun toSchematic(): Schematic {
        var xMin = Integer.MAX_VALUE
        var yMin = Integer.MAX_VALUE
        var zMin = Integer.MAX_VALUE
        var xMax = Integer.MIN_VALUE
        var yMax = Integer.MIN_VALUE
        var zMax = Integer.MIN_VALUE

        for (point in blockSet.keys) {
            if (point.blockX() < xMin) {
                xMin = point.blockX()
            }
            if (point.blockX() > xMax) {
                xMax = point.blockX()
            }
            if (point.blockY() < yMin) {
                yMin = point.blockY()
            }
            if (point.blockY() > yMax) {
                yMax = point.blockY()
            }
            if (point.blockZ() < zMin) {
                zMin = point.blockZ()
            }
            if (point.blockZ() > zMax) {
                zMax = point.blockZ()
            }
        }
        val xSize = xMax - xMin + 1
        val ySize = yMax - yMin + 1
        val zSize = zMax - zMin + 1
        val size: Point = Vec(xSize.toDouble(), ySize.toDouble(), zSize.toDouble())

        // Map of Block -> Palette ID

        // Map of Block -> Palette ID
        val paletteMap: HashMap<Block, Int> = HashMap()


        // Determine if we have air in our palette
        // If the number of blocks in our blockset is equal to our size, we know we shouldn't fill in air as default since we have taken up every space
        if(xSize * ySize * zSize > blockSet.size) {
            paletteMap[Block.AIR] = 0;
        }

        // This is horribly memory and space inefficient, but I cannot think of a better way of doing this right now
        // This is horribly memory and space inefficient, but I cannot think of a better way of doing this right now
        var blockBytes: ByteBuffer = ByteBuffer.allocate(1024)
        val pointSet: Set<Point> = blockSet.keys

        for (x in xMin..xMax) {
            for (y in yMin..yMax) {
                for (z in zMin..zMax) {
                    // Should be okay, since this is a short
                    // Also matt said so
                    if (blockBytes.remaining() <= 3) {
                        val oldBytes = blockBytes.array()
                        blockBytes = ByteBuffer.allocate(blockBytes.capacity() * 2)
                        blockBytes.put(oldBytes)
                    }
                    var foundPoint = false
                    for (point in pointSet) {
                        if (point.blockX() == x && point.blockY() == y && point.blockZ() == z) {
                            val block = blockSet[point]
                            var blockId: Int
                            if (!paletteMap.containsKey(block)) {
                                blockId = paletteMap.size
                                paletteMap[block!!] = paletteMap.size
                            } else {
                                blockId = paletteMap[block]!!
                            }
                            foundPoint = true
                            Utils.writeVarInt(blockBytes, blockId)
                            break
                        }
                    }
                    if (!foundPoint) {
                        Utils.writeVarInt(blockBytes, 0)
                    }
                }
            }
        }

        val palette = arrayOfNulls<Block>(paletteMap.size)
        for (entry in paletteMap.entries) {
            palette[entry.value] = entry.key
        }
        return Schematic(
            size,
            offset,
            palette,
            blockBytes.array()
        )

    }

}