package net.swordcraft.server.schematic

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.batch.BatchOption
import net.minestom.server.instance.batch.RelativeBlockBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Utils
import java.nio.ByteBuffer
import java.util.function.BiConsumer
import java.util.function.Function
import kotlin.math.abs

data class Schematic(
    private val size: Point,
    private val offset: Point,
    private val palette1: Array<Block?>,
    private val blocks1: ByteArray,
){
    private val palette: Array<Block?> = palette1.copyOf(palette1.size)
    private val blocks: ByteArray = blocks1.copyOf(blocks1.size)



    fun palette(): Array<Block?> {
        return palette.copyOf(palette.size)
    }

    fun blocks(): ByteArray {
        return blocks.copyOf(blocks.size)
    }

    fun size(rotation: Rotation): Point {
        val rotatedPos: Point = rotatePos(size, rotation)
        return Vec(
            abs(rotatedPos.blockX()).toDouble(),
            abs(rotatedPos.blockY()).toDouble(),
            abs(rotatedPos.blockZ()).toDouble()
            )
    }

    fun offset(rotation: Rotation): Point {
        return rotatePos(offset, rotation)
    }

    fun build(rotation: Rotation, modifier: Function<Block, Block>?): RelativeBlockBatch {
        val batch = RelativeBlockBatch(BatchOption().setCalculateInverse(true))
        apply(rotation) { pos, block ->
            batch.setBlock(
                pos,
                modifier?.apply(block) ?: block
            )
        }
        return batch
    }

    fun apply(rotation: Rotation, consumer: BiConsumer<Point, Block>) {
        val blocks: ByteBuffer = ByteBuffer.wrap(this.blocks)
        for (y in 0 until size.y().toInt()) {
            for (z in 0 until size.z().toInt()) {
                for (x in 0 until size.x().toInt()) {
                    val blockVal = Utils.readVarInt(blocks)
                    val b = palette[blockVal]
                    if (b == null || b.isAir) continue
                    val blockPos = Vec(x + offset.x(), y + offset.y(), z + offset.z())
                    consumer.accept(rotatePos(blockPos, rotation), rotateBlock(b, rotation))
                }
            }
        }
    }

    companion object {

        fun rotateBlock(block: Block, rotation: Rotation): Block {
            if (rotation == Rotation.NONE) return block
            var newBlock: Block = block
            if (block.getProperty("facing") != null) {
                newBlock = rotateFacing(block, rotation)
            }
            if (block.getProperty("north") != null) {
                newBlock = rotateFence(block, rotation)
            }
            return newBlock
        }

        private fun rotateFence(block: Block, rotation: Rotation): Block {
            return when (rotation) {
                Rotation.NONE -> block
                Rotation.CLOCKWISE_90 -> {
                    block.withProperties(mutableMapOf(
                        "north" to block.getProperty("west"),
                        "east" to block.getProperty("north"),
                        "south" to block.getProperty("east"),
                        "west" to block.getProperty("south")
                    ))
                }
                Rotation.CLOCKWISE_180 -> {
                    block.withProperties(mutableMapOf(
                        "north" to block.getProperty("south"),
                        "east" to block.getProperty("west"),
                        "south" to block.getProperty("north"),
                        "west" to block.getProperty("east")
                    ))
                }
                Rotation.CLOCKWISE_270 -> {
                    block.withProperties(mutableMapOf(
                        "north" to block.getProperty("east"),
                        "east" to block.getProperty("south"),
                        "south" to block.getProperty("west"),
                        "west" to block.getProperty("north")
                    ))
                }
            }
        }

        private fun rotate90(input: String): String {
            return when (input) {
                "north" -> "east"
                "east" -> "south"
                "south" -> "west"
                else -> "north"
            }
        }

        private fun rotateFacing(block: Block, rotation: Rotation): Block {
            return when (rotation) {
                Rotation.NONE -> block
                Rotation.CLOCKWISE_90 -> block.withProperty("facing", rotate90(block.getProperty("facing")))
                Rotation.CLOCKWISE_180 -> block.withProperty("facing", rotate90(rotate90(block.getProperty("facing"))))
                Rotation.CLOCKWISE_270 -> block.withProperty("facing", rotate90(rotate90(rotate90(block.getProperty("facing")))))
            }
        }

        private fun rotatePos(point: Point, rotation: Rotation): Point {
            return when (rotation) {
                Rotation.NONE -> point
                Rotation.CLOCKWISE_90 -> Vec(-point.z(), point.y(), point.x())
                Rotation.CLOCKWISE_180 -> Vec(-point.x(), point.y(), -point.z())
                Rotation.CLOCKWISE_270 -> Vec(point.z(), point.y(), -point.x())
            }
        }
    }



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Schematic

        if (size != other.size) return false
        if (offset != other.offset) return false
        if (!palette.contentEquals(other.palette)) return false
        if (!blocks.contentEquals(other.blocks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + offset.hashCode()
        result = 31 * result + palette.contentHashCode()
        result = 31 * result + blocks.contentHashCode()
        return result
    }
}